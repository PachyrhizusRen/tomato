package com.forufamily.gradle.plugin.config

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Project

class Configurations {
    private def variants
    private def bootClasspath

    Configurations(Project project) {
        def hasApp = project.plugins.withType(AppPlugin)
        def hasLib = project.plugins.withType(LibraryPlugin)
        if (!hasApp && !hasLib) {
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        }

        if (hasApp) {
            variants = project.android.applicationVariants
        } else {
            variants = project.android.libraryVariants
        }

        if (project.android.hasProperty('bootClasspath')) {
            bootClasspath = project.android.bootClasspath
        } else {
            bootClasspath = plugin.runtimeJarList
        }
    }

    DomainObjectCollection<BaseVariant> getVariants() {
        return variants
    }

    List<File> getBootClasspath() {
        return bootClasspath
    }
}
