package com.forufamily.gradle.plugin.tasks

import com.forufamily.gradle.plugin.config.Configurations
import com.forufamily.gradle.plugin.tasks.aj.Callable
import com.forufamily.gradle.plugin.tasks.aj.java.JavaSourceGenerator
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class AjFileProcessTask {

    static void attach(Project project) {
        project.afterEvaluate {
            def configuration = new Configurations(project)
            configuration.variants.all { variant ->
                final JavaCompile javaCompile = variant.hasProperty('javaCompiler') ? variant.javaCompiler : variant.javaCompile
                javaCompile.doFirst {
                    Callable callable = new JavaSourceGenerator(project, javaCompile, variant.buildType)//new AjFileCompiler(project, javaCompile).call()
                    callable.call()
                }
            }
        }
    }
}
