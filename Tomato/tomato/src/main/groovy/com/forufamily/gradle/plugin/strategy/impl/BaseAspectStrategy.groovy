package com.forufamily.gradle.plugin.strategy.impl

import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.forufamily.gradle.plugin.ajc.Worker
import com.forufamily.gradle.plugin.strategy.WeaveStrategy

import java.util.regex.Pattern

abstract class BaseAspectStrategy implements WeaveStrategy {
    protected final Worker worker
    protected final Collection<TransformInput> inputs
    protected final TransformOutputProvider provider
    private final List<String> excludedJars

    BaseAspectStrategy(Worker worker, Collection<TransformInput> inputs, TransformOutputProvider provider, List<String> excludedJars) {
        this.worker = worker
        this.inputs = inputs
        this.provider = provider
        this.excludedJars = excludedJars
    }

    protected boolean exclude(JarInput content) {
        boolean result = excludedJars.find { Pattern.matches(it, content.name) }
        if (result) "[$content.name] is in excludedJar list, so skips the weave action.".info()
        return result
    }

    @Override
    void start() {
        inputs.each {
            it.directoryInputs.each { dir ->
                worker.config {
                    aspectPath << dir.file
                    classPath << dir.file
                }
                //dir.debugFileStatus()
            }

            it.jarInputs.each { jar ->
                worker.config {
                    aspectPath << jar.file// 所有jar中的切面也可以工作
                    classPath << jar.file
                }
                //jar.debugFileStatus()
            }
        }

        extractFiles()
    }

    protected void doWork(QualifiedContent content) {
        "".info()
        "开始进行[${content.file.absolutePath}]织入".info()
        "[ajc.aspectPath]:${worker.aspectPath}\n[ajc.inPath]:${worker.inPath}\n[ajc.classPath]:${worker.classPath}".info()
        //content.debugFileStatus()
        worker.doWork()
        //content.debugFileStatus()
        "[${content.file.absolutePath}]织入完毕".info()
    }

    @Override
    Worker worker() {
        return worker
    }

    // 提取inPath参数
    protected abstract void extractFiles()
}