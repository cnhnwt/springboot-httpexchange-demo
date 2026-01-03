/**
 * 包名重命名脚本
 * 
 * 将 OpenAPI Generator 生成的固定包名重命名为目标包名
 * 从: org.cnhnwt.client.generated.api/model
 * 到: org.cnhnwt.client.{server-name}.api/model
 */

def clientPackage = project.properties['client.package']
def outputDir = project.properties['client.output.dir']

if (!clientPackage) {
    throw new IllegalStateException("Property 'client.package' is not set")
}

if (!outputDir) {
    throw new IllegalStateException("Property 'client.output.dir' is not set")
}

println "========================================="
println "重命名包名"
println "========================================="
println "目标包名: ${clientPackage}"
println "输出目录: ${outputDir}"

def generatedDir = new File(outputDir, 'generated/src/main/java')

if (!generatedDir.exists()) {
    println "警告: 生成目录不存在: ${generatedDir.absolutePath}"
    return
}

// 源包路径: org/cnhnwt/client/generated
def sourcePackagePath = new File(generatedDir, 'org/cnhnwt/client/generated')

if (!sourcePackagePath.exists()) {
    println "警告: 源包路径不存在: ${sourcePackagePath.absolutePath}"
    return
}

// 目标包路径: org/cnhnwt/client/{server-name}
def targetPackagePath = clientPackage.replace('.', '/')
def targetDir = new File(generatedDir, targetPackagePath)

println "源包路径: ${sourcePackagePath.absolutePath}"
println "目标包路径: ${targetDir.absolutePath}"

// 创建目标目录
targetDir.mkdirs()

// 移动 api 和 model 目录
['api', 'model'].each { subDir ->
    def sourceSubDir = new File(sourcePackagePath, subDir)
    def targetSubDir = new File(targetDir, subDir)
    
    if (sourceSubDir.exists()) {
        println "移动 ${subDir}: ${sourceSubDir.absolutePath} -> ${targetSubDir.absolutePath}"
        
        // 移动目录
        sourceSubDir.renameTo(targetSubDir)
        
        // 更新文件中的包名声明和 import 语句
        targetSubDir.eachFileRecurse { file ->
            if (file.name.endsWith('.java')) {
                def content = file.text
                def newContent = content
                    .replaceAll(
                        'package org\\.cnhnwt\\.client\\.generated\\.',
                        "package ${clientPackage}."
                    )
                    .replaceAll(
                        'import org\\.cnhnwt\\.client\\.generated\\.',
                        "import ${clientPackage}."
                    )
                
                if (content != newContent) {
                    file.text = newContent
                    println "  更新包名和导入: ${file.name}"
                }
            }
        }
    } else {
        println "警告: 子目录不存在: ${sourceSubDir.absolutePath}"
    }
}

// 删除空的 generated 目录
def generatedPackageDir = new File(generatedDir, 'org/cnhnwt/client/generated')
if (generatedPackageDir.exists() && generatedPackageDir.list().length == 0) {
    generatedPackageDir.deleteDir()
    println "删除空目录: ${generatedPackageDir.absolutePath}"
}

println "包名重命名完成"
println "========================================="