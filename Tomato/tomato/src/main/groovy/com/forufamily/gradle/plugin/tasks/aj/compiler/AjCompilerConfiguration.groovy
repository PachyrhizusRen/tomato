package com.forufamily.gradle.plugin.tasks.aj.compiler

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import org.aspectj.ajde.core.ICompilerConfiguration
import org.aspectj.ajde.core.IOutputLocationManager
import org.aspectj.ajde.core.JavaOptions
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.util.PatternSet

class AjCompilerConfiguration implements ICompilerConfiguration {
    static final PatternSet PATTERN_SET = new PatternSet().include(["**/*.aj"])
    private final IOutputLocationManager outputLocationManager
    private final Project project
    private final JavaCompile javaCompile

    AjCompilerConfiguration(Project project, JavaCompile javaCompile) {
        this.project = project
        this.javaCompile = javaCompile
        outputLocationManager = new AjOutputLocationManager(javaCompile)
    }

    @Override
    Map<String, String> getJavaOptionsMap() {
        Map<String, String> options = JavaOptions.getDefaultJavaOptions()
        options.put(JavaOptions.SOURCE_COMPATIBILITY_LEVEL, javaCompile.sourceCompatibility)
        options.put(JavaOptions.TARGET_COMPATIBILITY_LEVEL, javaCompile.targetCompatibility)
        return options
    }

    @Override
    String getNonStandardOptions() {
        // We just use standard options
        ""
    }

    @Override
    List<String> getProjectSourceFiles() {
        def extensions = project.plugins.hasPlugin(AppPlugin) ? project.extensions.getByType(AppExtension) : project.extensions.getByType(LibraryExtension)

        List<Object> sourceSet = new ArrayList<>()
        extensions.sourceSets.each {
            it.java.srcDirs.each {
                println "sourceSet:$it"
                sourceSet << it
            }
        }

        def list = project.files(sourceSet as Object[]).getAsFileTree().matching(PATTERN_SET)
                .toList()
                .stream()
                .map { it.absolutePath }
                .collect()
        "aj文件列表:$list".info()
        return list
    }

    @Override
    List<String> getProjectXmlConfigFiles() {
        Collections.emptyList()
    }

    @Override
    List<File> getProjectSourceFilesChanged() {
        // We do not know which file is changed. so let compiler make decision
        null
    }

    @Override
    String getClasspath() {
        return javaCompile.classpath.join(File.pathSeparator)
    }

    @Override
    IOutputLocationManager getOutputLocationManager() {
        outputLocationManager
    }

    @Override
    Set<File> getInpath() {
        // We do not want to weave aspect, we just want to call .ja file. so let it default
        return null
    }

    @Override
    String getOutJar() {
        // up
        return null
    }

    @Override
    Set<File> getAspectPath() {
        // up
        return null
    }

    @Override
    Map<String, File> getSourcePathResources() {
        // We do not process resources
        return null
    }

    @Override
    int getConfigurationChanges() {
        NO_CHANGES
    }

    @Override
    void configurationRead() {
        println "Configuration read, build start."
    }

    @Override
    List<String> getClasspathElementsWithModifiedContents() {
        // I do not care
        return null
    }

    @Override
    String getProjectEncoding() {
        "UTF-8"
    }

    @Override
    String getProcessor() {
        // We do not have a processor
        return null
    }

    @Override
    String getProcessorPath() {
        // up
        return null
    }
}
