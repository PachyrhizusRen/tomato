package com.forufamily.gradle.plugin.tasks.aj.compiler

import org.aspectj.ajde.core.IOutputLocationManager
import org.gradle.api.tasks.compile.JavaCompile

class AjOutputLocationManager implements IOutputLocationManager {
    JavaCompile javaCompile

    AjOutputLocationManager(JavaCompile javaCompile) {
        this.javaCompile = javaCompile
    }

    @Override
    File getOutputLocationForClass(File compilationUnit) {
        return javaCompile.destinationDir
    }

    @Override
    String getSourceFolderForFile(File sourceFile) {
        null
    }

    @Override
    File getOutputLocationForResource(File resource) {
        return javaCompile.destinationDir
    }

    @Override
    List<File> getAllOutputLocations() {
        return [javaCompile.destinationDir]
    }

    @Override
    File getDefaultOutputLocation() {
        return javaCompile.destinationDir
    }

    @Override
    void reportFileWrite(String file, int fileType) {
        println "写入文件[$file], 类型[$fileType]"
    }

    @Override
    Map<File, String> getInpathMap() {
        return null
    }

    @Override
    void reportFileRemove(String file, int fileType) {
        println "删除文件[$file], 类型[$fileType]"
    }

    @Override
    int discoverChangesSince(File dir, long buildtime) {
        return 0
    }
}
