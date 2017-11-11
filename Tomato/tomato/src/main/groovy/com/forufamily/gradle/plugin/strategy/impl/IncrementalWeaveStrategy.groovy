package com.forufamily.gradle.plugin.strategy.impl

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.forufamily.gradle.plugin.ajc.Worker
import com.forufamily.gradle.plugin.util.Utils
import com.google.common.collect.Sets

class IncrementalWeaveStrategy extends BaseAspectStrategy {

    IncrementalWeaveStrategy(Worker worker, Collection<TransformInput> inputs, TransformOutputProvider provider, List<String> excludedJars) {
        super(worker, inputs, provider, excludedJars)
    }

    @Override
    void extractFiles() {
        inputs.each {
            // Jar文件处理
            def jars = it.jarInputs
            jars.each { jar ->
                switch (jar.status) {
                    case Status.ADDED:
                    case Status.CHANGED:
                        if (jar.scopes.contains(QualifiedContent.Scope.SUB_PROJECTS) || exclude(jar)) {
                            // 如果是子项目(Library)，直接复制，子项目做自己的织入
                            Utils.copyJar(jar, provider)
                        } else {
                            def output = Utils.getOutputPath(provider, jar)
                            File ajcOutput = output.absolutePath.append("_").toFile()
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
                        break
                    case Status.REMOVED:
                        Utils.removeJar(jar, provider)
                        break
                }
            }

            // 目录处理
            def dirs = it.directoryInputs
            dirs.each { DirectoryInput dir ->
                worker.config {
                    //aspectPath.clear(), 不清理aspectPath, 让目录的切面在其他目录也有效
                    inPath.clear()
                    destination = Utils.getOutputPath(provider, dir).absolutePath

                    // 如果有改变的
                    Set<Status> statuses = Sets.newHashSet(dir.getChangedFiles().values())
                    if (statuses.contains(Status.CHANGED) || statuses.contains(Status.ADDED)) {
                        // 将改变的class文件目录放入到inPath中
                        inPath << dir.file.absolutePath
                    }
                }

                dir.changedFiles.each { file, status ->
                    switch (status) {
                        case Status.REMOVED:
                            // 移除输入目录中的文件
                            Utils.deleteFileFromOutput(dir, file, provider)
                            break
                    }
                }
                doWork(dir)
            }
        }
    }
}
