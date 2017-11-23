package com.forufamily.gradle.plugin.extension

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

        String.metaClass.firstLetterUpperCase << { ->
            delegate[0].toUpperCase() + (it.size() > 1 ?: delegate[1..-1])
        }
    }

    private static boolean inDebugMode() {
        return project.tomato.debug
    }
}