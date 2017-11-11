package com.forufamily.gradle.plugin.extension

class ProjectExtension {
    boolean debug = false
    /**
     * The jars we do not want to weave the aspects in this list.
     * <p><strong>e.g</strong>:
     * <p>'<i>android.local.jars</i>'&nbsp;&nbsp;&nbsp;&nbsp;mean excludes all local jars. Cause all the local JarInput name startWith it.
     * <p>'<i>android.support.v4</i>'&nbsp;&nbsp;&nbsp;&nbsp;mean excludes all jars which name startWith("android.support.v4").
     */
    def excludedJars = new ArrayList<String>()
    def ajcArgs = new ArrayList<String>()

    def getDebug() {
        return debug
    }

    def setDebug(boolean debug) {
        this.debug = debug
    }

    def getExcludedJars() {
        return excludedJars
    }

    def setExcludedJars(List<String> excludedJars) {
        this.excludedJars.addAll excludedJars ?: []
    }

    def getAjcArgs() {
        return ajcArgs
    }

    def setAjcArgs(List<String> ajcArgs) {
        this.ajcArgs.addAll(ajcArgs ?: [])
    }
}
