/**
 * 模板占位符替换脚本
 * 
 * 功能：
 * 1. 读取所有模板文件
 * 2. 替换占位符为实际值
 * 3. 输出到目标目录
 * 
 * 占位符列表：
 * - @CLIENT_PACKAGE@ - 客户端包名（例如：org.cnhnwt.client.httpexchange）
 * - @SERVER_ARTIFACT_ID@ - 服务端 artifactId（例如：httpexchange-server）
 * - @SERVER_ARTIFACT_ID_CAMEL@ - 驼峰命名的服务端 artifactId（例如：HttpexchangeServer）
 * - @SERVER_ARTIFACT_ID_CAMEL_LOWER@ - 首字母小写的驼峰命名（例如：httpexchangeServer）
 * - @CLIENT_VERSION@ - 客户端版本号
 */

import java.nio.file.*

// 获取 Maven 属性
def clientPackage = project.properties['client.package']
def clientArtifactId = project.properties['client.artifactId']
def serverArtifactId = project.properties['server.artifactId']
def clientVersion = project.properties['client.version']

// 生成驼峰命名
def serverArtifactIdCamel = toCamelCase(serverArtifactId, true)
def serverArtifactIdCamelLower = toCamelCase(serverArtifactId, false)

println "========================================="
println "模板替换配置："
println "  Client Package: ${clientPackage}"
println "  Client ArtifactId: ${clientArtifactId}"
println "  Server ArtifactId: ${serverArtifactId}"
println "  Server ArtifactId (Camel): ${serverArtifactIdCamel}"
println "  Server ArtifactId (camelLower): ${serverArtifactIdCamelLower}"
println "  Client Version: ${clientVersion}"
println "========================================="

// 定义模板目录和输出目录
def templatesDir = new File(project.basedir, 'templates')
def outputBaseDir = new File(project.build.directory, 'generated-sources/templates')

// 确保输出目录存在
outputBaseDir.mkdirs()

// 处理所有模板文件
def templateFiles = [
    'ClientProperties.java.template',
    'ClientAutoConfiguration.java.template',
    'HeaderPropagationInterceptor.java.template',
    'HttpExchangeApiScanner.java.template',
    'HttpExchangeApiBeanRegistrar.java.template',
    'AutoConfiguration.imports.template',
    'client-pom-template.xml'
]

templateFiles.each { templateFileName ->
    def templateFile = new File(templatesDir, templateFileName)
    
    if (!templateFile.exists()) {
        println "警告: 模板文件不存在: ${templateFile}"
        return
    }
    
    println "处理模板: ${templateFileName}"
    
    // 读取模板内容
    def content = templateFile.text
    
    // 替换占位符
    content = content
        .replace('@CLIENT_PACKAGE@', clientPackage)
        .replace('@CLIENT_ARTIFACT_ID@', clientArtifactId)
        .replace('@SERVER_ARTIFACT_ID@', serverArtifactId)
        .replace('@SERVER_ARTIFACT_ID_CAMEL@', serverArtifactIdCamel)
        .replace('@SERVER_ARTIFACT_ID_CAMEL_LOWER@', serverArtifactIdCamelLower)
        .replace('@CLIENT_VERSION@', clientVersion)
    
    // 确定输出文件路径
    def outputFile
    if (templateFileName.endsWith('.java.template')) {
        // Java 文件：输出到包目录
        def packagePath = clientPackage.replace('.', '/')
        def outputDir = new File(outputBaseDir, "java/${packagePath}/config")
        outputDir.mkdirs()
        
        // 生成正确的文件名：将模板名中的占位符替换为实际值
        def outputFileName = templateFileName
            .replace('.template', '')
            .replace('Client', serverArtifactIdCamel)
            .replace('HeaderPropagation', serverArtifactIdCamel + 'HeaderPropagation')
        
        // 特殊处理：确保文件名与类名一致
        if (templateFileName == 'ClientProperties.java.template') {
            outputFileName = serverArtifactIdCamel + 'ClientProperties.java'
        } else if (templateFileName == 'ClientAutoConfiguration.java.template') {
            outputFileName = serverArtifactIdCamel + 'AutoConfiguration.java'
        } else if (templateFileName == 'HeaderPropagationInterceptor.java.template') {
            outputFileName = serverArtifactIdCamel + 'HeaderPropagationInterceptor.java'
        } else if (templateFileName == 'HttpExchangeApiScanner.java.template') {
            outputFileName = 'HttpExchangeApiScanner.java'
        } else if (templateFileName == 'HttpExchangeApiBeanRegistrar.java.template') {
            outputFileName = 'HttpExchangeApiBeanRegistrar.java'
        }
        
        outputFile = new File(outputDir, outputFileName)
        
    } else if (templateFileName == 'AutoConfiguration.imports.template') {
        // Spring Boot 3+ 自动配置文件
        def outputDir = new File(outputBaseDir, 'resources/META-INF/spring')
        outputDir.mkdirs()
        outputFile = new File(outputDir, 'org.springframework.boot.autoconfigure.AutoConfiguration.imports')
        
    } else if (templateFileName == 'client-pom-template.xml') {
        // POM 文件
        outputFile = new File(outputBaseDir, 'pom.xml')
        
    } else {
        // 其他文件
        outputFile = new File(outputBaseDir, templateFileName.replace('.template', ''))
    }
    
    // 写入输出文件
    outputFile.text = content
    println "  -> 输出到: ${outputFile}"
}

println "========================================="
println "模板替换完成！"
println "输出目录: ${outputBaseDir}"
println "========================================="

/**
 * 转换为驼峰命名
 * 
 * @param str 输入字符串（例如：httpexchange-server）
 * @param capitalizeFirst 是否首字母大写
 * @return 驼峰命名字符串（例如：HttpexchangeServer 或 httpexchangeServer）
 */
def toCamelCase(String str, boolean capitalizeFirst) {
    if (str == null || str.isEmpty()) {
        return str
    }
    
    def parts = str.split('[-_]')
    def result = new StringBuilder()
    
    parts.eachWithIndex { part, index ->
        if (index == 0 && !capitalizeFirst) {
            result.append(part.toLowerCase())
        } else {
            result.append(part.substring(0, 1).toUpperCase())
            if (part.length() > 1) {
                result.append(part.substring(1).toLowerCase())
            }
        }
    }
    
    return result.toString()
}