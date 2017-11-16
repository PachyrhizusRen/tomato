package com.forufamily.gradle.plugin.tasks.aj.compiler

import com.forufamily.gradle.plugin.tasks.aj.Callable
import org.aspectj.ajde.core.AjCompiler
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class AjFileCompiler implements Callable {
    private final JavaCompile javaCompile
    private final Project project
    private def configuration
    private def progressMonitor
    private def messageHandler

    AjFileCompiler(JavaCompile javaCompile, Project project) {
        this.javaCompile = javaCompile
        this.project = project
        this.configuration = new AjCompilerConfiguration(project, javaCompile)
        progressMonitor = new AjBuildProgressMonitor()
        messageHandler = new AjBuildMessageHandler()
    }

    @Override
    void call() {
        AjCompiler compiler = new AjCompiler("aj_compiler", configuration, progressMonitor, messageHandler)
        compiler.buildFresh()
    }
}
