package com.forufamily.gradle.plugin.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInvocation
import com.forufamily.gradle.plugin.ajc.Worker
import com.forufamily.gradle.plugin.strategy.WeaveStrategy
import com.forufamily.gradle.plugin.strategy.WeaveStrategyFactory
import com.forufamily.gradle.plugin.strategy.impl.LibraryWeaveStrategyProxy
import com.google.common.collect.ImmutableSet
import org.gradle.api.Project
/**
 * 1. 库项目只做class注入<br>
 * 2. 库项目不支持向jar中注入，如果有注入jar的需求，会在在app项目中进行。但是库项目中可以存在向jar中注入的切面，只是它的工作实际上会在App中进行。
 */
class LibraryAspectTransform extends BaseAspectTransform {

    LibraryAspectTransform(Project project) {
        super(project)
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return ImmutableSet.<QualifiedContent.Scope> of(QualifiedContent.Scope.PROJECT)
    }

    @Override
    Set<? super QualifiedContent.Scope> getReferencedScopes() {
        return ImmutableSet.<QualifiedContent.Scope> of(
                QualifiedContent.Scope.EXTERNAL_LIBRARIES
        )
    }

    @Override
    protected void doTransform(TransformInvocation invocation, Worker worker) {
        "[$projectName]开始织入切面--------------------------------------".info()

        debugInputs(invocation.inputs)
        WeaveStrategy strategy = LibraryWeaveStrategyProxy
                .wrap(WeaveStrategyFactory.create(worker, invocation, excludedJars()))
                .invocation(invocation)
        strategy.start()

        "[$projectName]切面织入完毕--------------------------------------".info()
    }
}
