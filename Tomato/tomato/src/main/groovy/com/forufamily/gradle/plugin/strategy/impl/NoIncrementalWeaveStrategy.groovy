package com.forufamily.gradle.plugin.strategy.impl

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.forufamily.gradle.plugin.ajc.Worker
import com.forufamily.gradle.plugin.util.Utils

class NoIncrementalWeaveStrategy extends BaseAspectStrategy {

    NoIncrementalWeaveStrategy(Worker worker, Collection<TransformInput> inputs, TransformOutputProvider provider) {
        super(worker, inputs, provider)
    }

    @Override
    void extractFiles() {
        inputs.each {
            // Jar文件处理
            it.jarInputs.each { jar ->
                if (jar.scopes.contains(QualifiedContent.Scope.SUB_PROJECTS)) {
                    // 如果是子项目(Library)，直接复制，子项目做自己的织入
                    Utils.copyJar(jar, provider)
                } else {
                    def output = Utils.getOutputPath(provider, jar)
                    def ajcOutput = output.absolutePath.append("_").toFile()
                    if (ajcOutput.exists()) ajcOutput.deleteDir()
                    // 如果不是子项目, 进行Jar文件织入
                    worker.config {
                        inPath.clear()
                        // 将输出路径加上一个下划线
                        destination = ajcOutput.absolutePath
                        inPath << jar.file
                    }
                    doWork(jar)

                    // 合并ajc输出目录中的文件class文件到输出目录
                    Utils.mergeJar(ajcOutput, output)
                    ajcOutput.deleteDir()
                }
            }

            // 目录处理
            it.directoryInputs.each { dir ->
                worker.config {
                    //aspectPath.clear(), 不清理aspectPath, 让目录的切面在其他目录也有效
                    inPath.clear()
                    destination = Utils.getOutputPath(provider, dir).absolutePath
                    // 将目录放入到inPath中
                    inPath << dir.file
                }
                doWork(dir)
            }
        }
    }
}
