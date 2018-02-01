package com.forufamily.gradle.plugin.extension

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInvocation
import com.forufamily.gradle.plugin.util.FileLockFinder
import org.gradle.api.Project

class LangExtensions {
    private static Project project

    static void init(Project project) {
        this.project = project
        String.metaClass.append << { text ->
            new StringBuilder(delegate).append(text).toString()
        }

        String.metaClass.toFile << { ->
            new File(delegate)
        }

        String.metaClass.info << { ->
            if (inDebugMode()) println(delegate)
        }

        TransformInvocation.metaClass.debugFileStatus << { ->
            "--FileLockTracker start".info()
            delegate.inputs.each { input ->
                FileLockFinder.findLockedFiles(input.jarInputs.stream().map{it.file}.collect(), "----")
                input.directoryInputs.each { new FileLockFinder(it.file, "----").start() }
            }
            "--FileLockTracker stop".info()
        }

        TransformInvocation.metaClass.hasLockedFils << { ->
            delegate.inputs.find { input ->
                input.jarInputs.find { FileLockFinder.isFileLocked(it.file) } || input.directoryInputs.find { FileLockFinder.isFileLocked(it.file) }
            }
        }

        QualifiedContent.metaClass.debugFileStatus << { ->
            "--FileLockTracker start".info()
            FileLockFinder.findLockedFile(delegate.file, "----")
            "--FileLockTracker stop".info()
        }
    }

    private static boolean inDebugMode() {
        return project.tomato.debug
    }
}