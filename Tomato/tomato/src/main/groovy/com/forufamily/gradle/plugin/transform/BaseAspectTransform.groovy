package com.forufamily.gradle.plugin.transform

import com.android.build.api.transform.*
import com.forufamily.gradle.plugin.ajc.Worker
import com.forufamily.gradle.plugin.util.Utils
import com.google.common.collect.ImmutableSet
import org.gradle.api.Project

abstract class BaseAspectTransform extends Transform {
    protected static final String ASPECTJ_RT = "aspectjrt"
    private Project project
    private Worker worker

    BaseAspectTransform(Project project) {
        this.project = project
        worker = new Worker(project)
    }

    List<String> excludedJars() {
        return project.tomato.excludedJars
    }

    String getProjectName() {
        return project?.name
    }

    @Override
    String getName() {
        return "tomato"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return ImmutableSet.<QualifiedContent.ContentType> of(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation invocation) throws TransformException, InterruptedException, IOException {
        "开始转换, 增量转换[${invocation.incremental}]".info()
        def hasAspectJrt = hasAspectJrt(invocation)
        // 如果Transform不支持增量，则清理输出目录
        if (!invocation.incremental) invocation.outputProvider.deleteAll()

        if (hasAspectJrt) {
            "项目[${project.name}]中发现AspectJRT-------------------".info()
            //invocation.debugFileStatus()
            doTransform(invocation, worker)
            //invocation.debugFileStatus()
            // If file was locked, we call gc and try to release it.
            (0..60).any {
                if (invocation.hasLockedFils()) {
                    "some file is locked, try call to release it.".info()
                    System.gc()
                    Thread.sleep(1000)
                } else {
                    "all files are released.".info()
                    return true
                }
            }
        } else {
            "项目[${project.name}]中没有发现AspectJRT-------------------".info()
            invocation.inputs.each { input ->
                Utils.copyDirectory(input, invocation.outputProvider)
                Utils.copyJars(input, invocation.outputProvider)
            }
        }
    }

    // 判断当前是否包含Aspectj运行时
    protected static boolean hasAspectJrt(TransformInvocation invocation) {
        for (transformInput in invocation.referencedInputs) {
            for (jarInput in transformInput.jarInputs) {
                // After Desugar transform, the file's name was changed, but the 'JarInput' would be contain right info.
                if (jarInput.name.toLowerCase().contains(ASPECTJ_RT)
                        || jarInput.file.absolutePath.toLowerCase().contains(ASPECTJ_RT)) {
                    return true
                }
            }
        }

        // debug
        "判断Aspectjrt调试信息--------------------------".info()
        invocation.referencedInputs.each {
            it.jarInputs.each {
                "${it.file.absolutePath}".info()
            }
        }
        "判断Aspectjrt调试信息--------------------------".info()
        return false
    }

    // 输入文件列表Debug信息
    protected static void debugInputs(Collection<TransformInput> inputs) {
        inputs.each {
            def dirs = it.directoryInputs
            dirs.each {
                "".info()
                "当前目录:${it.name}".info()
                "目录[${it.name}]文件变动数量:${it.changedFiles.size()}".info()
                "目录[${it.name}]文件变动列表:-----------------------START".info()
                it.changedFiles.each { file, status ->
                    "文件[${file.name}], 状态:${status}".info()
                }
                "目录[${it.name}]文件变动列表:-----------------------END".info()
            }

            def jars = it.jarInputs
            jars.each {
                "Jar文件[${it.name}], 状态:${it.status}".info()
            }
        }
    }

    // 准备工作完毕，开始处理
    protected abstract void doTransform(TransformInvocation invocation, Worker worker)
}
