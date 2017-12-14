package com.forufamily.gradle.plugin.util

import com.android.annotations.NonNull
import com.android.build.api.transform.*
import com.android.builder.packaging.JarMerger
import com.android.builder.packaging.ZipEntryFilter
import com.google.common.io.Files
import org.apache.commons.io.FileUtils

class Utils {

    @NonNull
    static File getOutputPath(
            @NonNull TransformOutputProvider outputProvider, @NonNull QualifiedContent content) {
        return outputProvider
                .getContentLocation(
                content.getName(),
                content.getContentTypes(),
                content.getScopes(),
                content.getFile().isDirectory() ? Format.DIRECTORY : Format.JAR)
    }

    // 复制所有jar文件(Input -> Output)
    static void copyJars(TransformInput input, TransformOutputProvider provider) {
        input.jarInputs.each {
            copyJar(it, provider)
        }
    }

    // 复制整个目录(Input -> Output)
    static void copyDirectory(TransformInput input, TransformOutputProvider provider) {
        input.directoryInputs.each { dir ->
            def dest = getOutputPath(provider, dir)
            FileUtils.copyDirectory(dir.file, dest)
            "Copy directory:[${dir.name}]".info()
        }
    }

    // 复制单个jar文件(Input -> Output)
    static void copyJar(JarInput jar, TransformOutputProvider provider) {
        def dest = getOutputPath(provider, jar)
        Files.copy(jar.file, dest)
        "Copy jar file:[${jar.name}]".info()
    }

    // 删除单个jar文件 (Output)
    static void removeJar(JarInput jar, TransformOutputProvider provider) {
        def dest = getOutputPath(provider, jar)
        if (dest.exists() && dest.delete()) {
            "Delete jar file:[${jar.name}]".info()
        }
    }

    // 从输出目录删除文件
    static void deleteFileFromOutput(DirectoryInput sourceDirectory, File sourceFile, TransformOutputProvider provider) {
        def dest = getOutputPath(provider, sourceDirectory)
        File destFile = sourceFile.absolutePath.replace(sourceDirectory.file.absolutePath, dest.absolutePath).toFile()
        if (destFile.exists()) destFile.delete()
    }

    static void mergeJar(File input, File output) {
        "Merge files from [${input.absolutePath}] to [${output.absolutePath}]".info()
        JarMerger jarMerger = null
        try {
            jarMerger = new JarMerger(output.toPath(), ZipEntryFilter.CLASSES_ONLY)
            jarMerger.addDirectory(input.toPath())
        } catch (IOException e) {
            throw new TransformException(e)
        } finally {
            tryClose(jarMerger)
        }
    }

    static void tryClose(Closeable closeable) {
        try {
            closeable?.close()
        } catch (Throwable e) { e.printStackTrace()}
    }
}
