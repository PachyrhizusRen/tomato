package com.forufamily.gradle.plugin.strategy.impl

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.forufamily.gradle.plugin.ajc.Worker
import com.forufamily.gradle.plugin.strategy.WeaveStrategy

abstract class BaseAspectStrategy implements WeaveStrategy {
    protected final Worker worker
    protected final Collection<TransformInput> inputs
    protected final TransformOutputProvider provider

    BaseAspectStrategy(Worker worker, Collection<TransformInput> inputs, TransformOutputProvider provider) {
        this.worker = worker
        this.inputs = inputs
        this.provider = provider
    }

    @Override
    void start() {
        inputs.each {
            it.directoryInputs.each { dir ->
                worker.config {
                    aspectPath << dir.file
                    classPath << dir.file
                }
            }

            it.jarInputs.each { jar ->
                worker.config {
                    aspectPath << jar.file// 所有jar中的切面也可以工作
                    classPath << jar.file
                }
            }
        }

        extractFiles()
    }

    protected void doWork(QualifiedContent dir) {
        "".info()
        "开始进行[${dir.file.absolutePath}]织入".info()
        "[ajc.aspectPath]:${worker.aspectPath}\n[ajc.inPath]:${worker.inPath}\n[ajc.classPath]:${worker.classPath}".info()
        worker.doWork()
        "[${dir.file.absolutePath}]织入完毕".info()
    }

    @Override
    Worker worker() {
        return worker
    }

    // 提取inPath参数
    protected abstract void extractFiles()
}