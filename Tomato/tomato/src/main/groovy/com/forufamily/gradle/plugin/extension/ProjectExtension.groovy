package com.forufamily.gradle.plugin.extension

class ProjectExtension {
    boolean debug = false

    def getDebug() {
        return debug
    }

    void setDebug(boolean debug) {
        this.debug = debug
    }
}
