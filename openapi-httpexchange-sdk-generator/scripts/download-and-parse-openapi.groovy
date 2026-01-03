/**
 * OpenAPI 下载和解析脚本
 *
 * 支持两种模式：
 * 1. 从本地文件读取：-Dopenapi.file.path=openapi/server-name@version.json
 * 2. 从 URL 下载：-Dopenapi.url=http://localhost:8181/v3/api-docs -Dserver.artifactId=httpexchange-server -Dserver.version=0.0.1-SNAPSHOT
 *
 * 如果提供了 URL，会下载到 openapi 目录并按照命名规范保存
 * 如果没有提供任何参数，脚本会跳过执行（用于从根目录构建整个项目时）
 */

def openApiFilePath = project.properties['openapi.file.path'] ?: System.getProperty('openapi.file.path')
def openApiUrl = project.properties['openapi.url'] ?: System.getProperty('openapi.url')

// 如果没有提供任何参数，跳过执行
if (!openApiFilePath && !openApiUrl) {
    println "========================================="
    println "OpenAPI SDK 生成器 - 跳过"
    println "========================================="
    println "未提供 openapi.file.path 或 openapi.url 参数"
    println "如需生成客户端 SDK，请使用以下命令："
    println ""
    println "方式 1 (本地文件):"
    println "  cd openapi-httpexchange-sdk-generator"
    println "  ../mvnw clean install -Dopenapi.file.path=openapi/httpexchange-server@0.0.1-SNAPSHOT.json"
    println ""
    println "方式 2 (从 URL 下载):"
    println "  cd openapi-httpexchange-sdk-generator"
    println "  ../mvnw clean install -Dopenapi.url=http://localhost:8181/v3/api-docs -Dserver.artifactId=httpexchange-server"
    println "========================================="
    
    // 设置跳过标志，供后续插件检查
    project.properties['skip.client.generation'] = 'true'
    System.setProperty('skip.client.generation', 'true')
    return
}

println "========================================="
println "OpenAPI 下载和解析"
println "========================================="

File openApiSpecFile

// 模式 1: 从 URL 下载
if (openApiUrl) {
    println "模式: 从 URL 下载"
    println "URL: ${openApiUrl}"
    
    // 获取必需的参数
    def serverArtifactId = project.properties['server.artifactId'] ?: System.getProperty('server.artifactId')
    def serverVersion = project.properties['server.version'] ?: System.getProperty('server.version') ?: project.version
    
    if (!serverArtifactId) {
        throw new IllegalArgumentException(
            "使用 URL 模式时必须提供 server.artifactId\n" +
            "用法: -Dopenapi.url=http://localhost:8181/v3/api-docs -Dserver.artifactId=httpexchange-server [-Dserver.version=0.0.1-SNAPSHOT]"
        )
    }
    
    println "Server Artifact ID: ${serverArtifactId}"
    println "Server Version: ${serverVersion}"
    
    // 创建 openapi 目录
    def openapiDir = new File(project.basedir, 'openapi')
    if (!openapiDir.exists()) {
        openapiDir.mkdirs()
        println "创建目录: ${openapiDir.absolutePath}"
    }
    
    // 生成文件名: {server-artifactId}@{version}.json
    def fileName = "${serverArtifactId}@${serverVersion}.json"
    openApiSpecFile = new File(openapiDir, fileName)
    
    println "下载到: ${openApiSpecFile.absolutePath}"
    
    // 下载文件
    try {
        def url = new URL(openApiUrl)
        def connection = url.openConnection()
        connection.setRequestProperty("Accept", "application/json")
        
        openApiSpecFile.withOutputStream { out ->
            connection.inputStream.withStream { input ->
                out << input
            }
        }
        
        println "✓ 下载成功"
        
        // 设置 openapi.file.path 供后续使用
        openApiFilePath = "openapi/${fileName}"
        project.properties['openapi.file.path'] = openApiFilePath
        
    } catch (Exception e) {
        throw new RuntimeException("下载 OpenAPI 规范失败: ${e.message}", e)
    }
    
} else {
    // 模式 2: 从本地文件读取
    println "模式: 从本地文件读取"
    println "文件路径: ${openApiFilePath}"
    
    openApiSpecFile = new File(openApiFilePath)
    if (!openApiSpecFile.isAbsolute()) {
        openApiSpecFile = new File(project.basedir, openApiFilePath)
    }
    
    if (!openApiSpecFile.exists()) {
        throw new FileNotFoundException(
            "OpenAPI 规范文件不存在: ${openApiSpecFile.absolutePath}"
        )
    }
}

println "OpenAPI 规范文件: ${openApiSpecFile.absolutePath}"

// 解析文件名格式: {server-artifactId}@{version}.json
def openApiFileName = openApiSpecFile.name
println "文件名: ${openApiFileName}"

def matcher = openApiFileName =~ /^(.+)@(.+)\.json$/

if (!matcher.matches()) {
    throw new IllegalArgumentException(
        "无效的 OpenAPI 文件名格式\n" +
        "期望格式: {server-artifactId}@{version}.json\n" +
        "实际文件名: ${openApiFileName}"
    )
}

def serverArtifactId = matcher[0][1]
def serverVersion = matcher[0][2]

// 生成 Client 相关属性
def clientArtifactId = "${serverArtifactId}-client"
def clientVersion = serverVersion
def clientPackage = "org.cnhnwt.client.${serverArtifactId.replaceAll('-', '.')}"

// 设置 Maven 属性（同时设置到 project.properties 和 System.properties）
project.properties['server.artifactId'] = serverArtifactId
System.setProperty('server.artifactId', serverArtifactId)

project.properties['server.version'] = serverVersion
System.setProperty('server.version', serverVersion)

project.properties['client.artifactId'] = clientArtifactId
System.setProperty('client.artifactId', clientArtifactId)

project.properties['client.version'] = clientVersion
System.setProperty('client.version', clientVersion)

project.properties['client.package'] = clientPackage
System.setProperty('client.package', clientPackage)

project.properties['openapi.spec.file'] = openApiSpecFile.absolutePath

// 设置生成目录
def generatedClientsDir = new File(project.build.directory, 'generated-clients')
def clientProjectDir = new File(generatedClientsDir, clientArtifactId)

project.properties['generated.clients.dir'] = generatedClientsDir.absolutePath
project.properties['client.project.dir'] = clientProjectDir.absolutePath

// 获取 Maven settings 文件路径（从 -s 参数或默认位置）
def mavenSettingsPath = project.properties['maven.settings.path'] ?: System.getProperty('maven.settings.path')

if (!mavenSettingsPath) {
    // 尝试从 Maven session 获取 settings 文件路径
    // 在 groovy-maven-plugin 中，session 对象是直接可用的
    try {
        def request = session.request
        
        // 获取用户 settings 文件
        def userSettingsFile = request.userSettingsFile
        if (userSettingsFile && userSettingsFile.exists()) {
            // 检查是否是非默认的 settings 文件（即用户通过 -s 指定的）
            def defaultUserSettings = new File(System.getProperty('user.home'), '.m2/settings.xml')
            if (userSettingsFile.absolutePath != defaultUserSettings.absolutePath) {
                mavenSettingsPath = userSettingsFile.absolutePath
                println "检测到自定义 Maven settings 文件: ${mavenSettingsPath}"
            }
        }
    } catch (Exception e) {
        println "无法获取 Maven session 信息: ${e.message}"
        e.printStackTrace()
    }
}

// 保存 settings 路径到属性中，供后续 exec-maven-plugin 使用
if (mavenSettingsPath) {
    project.properties['maven.settings.path'] = mavenSettingsPath
    System.setProperty('maven.settings.path', mavenSettingsPath)
    println "Maven Settings 路径: ${mavenSettingsPath}"
    
    // 将 settings 路径写入临时文件，供 exec-maven-plugin 读取
    def settingsFile = new File(project.build.directory, 'maven-settings-path.txt')
    settingsFile.parentFile.mkdirs()
    settingsFile.text = mavenSettingsPath
    println "Settings 路径已写入: ${settingsFile.absolutePath}"
}

println "========================================="
println "解析结果:"
println "  Server Artifact ID: ${serverArtifactId}"
println "  Server Version: ${serverVersion}"
println "  Client Artifact ID: ${clientArtifactId}"
println "  Client Version: ${clientVersion}"
println "  Client Package: ${clientPackage}"
println "  Client Project Dir: ${clientProjectDir.absolutePath}"
if (mavenSettingsPath) {
    println "  Maven Settings: ${mavenSettingsPath}"
}
println "========================================="