package com.forufamily.gradle.plugin.strategy.impl

import com.android.build.api.transform.TransformInvocation
import com.forufamily.gradle.plugin.strategy.WeaveStrategy
import com.forufamily.gradle.plugin.ajc.Worker

class LibraryWeaveStrategyProxy implements WeaveStrategy {
    private final WeaveStrategy target
    private TransformInvocation invocation

    static LibraryWeaveStrategyProxy wrap(WeaveStrategy target) {
        return new LibraryWeaveStrategyProxy(target)
    }

    private LibraryWeaveStrategyProxy(WeaveStrategy target) {
        this.target = target
    }

    LibraryWeaveStrategyProxy invocation(TransformInvocation invocation) {
        this.invocation = invocation
        return this
    }

    @Override
    void start() {
        setupLibrary()
        target?.start()
    }

    @Override
    Worker worker() {
        return target?.worker()
    }

    private void setupLibrary() {
        invocation?.referencedInputs?.each {
            it.jarInputs.each { jar ->
                target.worker().config {
                    aspectPath << jar.file
                    classPath << jar.file
                }
            }
        }
    }
}
