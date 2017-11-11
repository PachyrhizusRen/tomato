package com.forufamily.gradle.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.forufamily.gradle.plugin.extension.LangExtensions
import com.forufamily.gradle.plugin.extension.ProjectExtension
import com.forufamily.gradle.plugin.transform.AppAspectTransform
import com.forufamily.gradle.plugin.transform.LibraryAspectTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class AspectjPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        String.metaClass.methodMissing = { name, args ->
            "unknown method $name(${args.join(',')})"
        }

        project.repositories {
            mavenLocal()
        }

        project.dependencies {
            implementation 'org.aspectj:aspectjrt:1.8.12'
        }

        project.extensions.create("tomato", ProjectExtension)
        if (project.plugins.hasPlugin(AppPlugin)) {
            LangExtensions.init(project)// MetaClass方法扩展

            AppExtension app = project.extensions.getByType(AppExtension)
            app.registerTransform(new AppAspectTransform(project))
        } else if (project.plugins.hasPlugin(LibraryPlugin)) {
            LibraryExtension lib = project.extensions.getByType(LibraryExtension)
            lib.registerTransform(new LibraryAspectTransform(project))
        }
    }
}
