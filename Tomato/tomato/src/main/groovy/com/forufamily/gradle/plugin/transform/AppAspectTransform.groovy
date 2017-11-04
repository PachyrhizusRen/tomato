package com.forufamily.gradle.plugin.transform

import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.forufamily.gradle.plugin.ajc.Worker
import com.forufamily.gradle.plugin.strategy.WeaveStrategy
import com.forufamily.gradle.plugin.strategy.WeaveStrategyFactory
import com.google.common.collect.ImmutableSet
import org.gradle.api.Project

class AppAspectTransform extends BaseAspectTransform {

    AppAspectTransform(Project project) {
        super(project)
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        def name = QualifiedContent.Scope.PROJECT_LOCAL_DEPS.name()
        def deprecated = QualifiedContent.Scope.PROJECT_LOCAL_DEPS.getClass().getField(name).getAnnotation(Deprecated.class)

        // https://android.googlesource.com/platform/tools/base/+/gradle_3.0.0/build-system/gradle-api/src/main/java/com/android/build/api/transform/QualifiedContent.java
        if (deprecated == null) {
            // Gradle API 3.0之前的版本构建
            return ImmutableSet.<QualifiedContent.Scope> of(
                    QualifiedContent.Scope.PROJECT,
                    QualifiedContent.Scope.PROJECT_LOCAL_DEPS,
                    QualifiedContent.Scope.EXTERNAL_LIBRARIES,
                    QualifiedContent.Scope.SUB_PROJECTS,
                    QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS)
        } else {
            // Gradle API 3.0及之后的的版本构建，PROJECT_LOCAL_DEPS和SUB_PROJECTS_LOCAL_DEPS已经过期, 直接使用EXTERNAL_LIBRARIES就可以
            return ImmutableSet.<QualifiedContent.Scope> of(
                    QualifiedContent.Scope.PROJECT,
                    QualifiedContent.Scope.EXTERNAL_LIBRARIES,
                    QualifiedContent.Scope.SUB_PROJECTS)
        }
    }

    // 判断当前是否包含Aspectj运行时
    @Override
    protected boolean hasAspectJrt(TransformInvocation invocation) {
        for (TransformInput transformInput : invocation.inputs) {
            for (JarInput jarInput : transformInput.jarInputs) {
                if (jarInput.file.absolutePath.contains(ASPECTJ_RT)) {
                    return true
                }
            }
        }
        return false
    }

    @Override
    protected void doTransform(TransformInvocation invocation, Worker worker) {
        "[$projectName]开始织入切面--------------------------------------".info()

        debugInputs(invocation.inputs)
        WeaveStrategy strategy = WeaveStrategyFactory.create(worker, invocation)
        strategy.start()

        "[$projectName]切面织入完毕--------------------------------------".info()
    }
}
