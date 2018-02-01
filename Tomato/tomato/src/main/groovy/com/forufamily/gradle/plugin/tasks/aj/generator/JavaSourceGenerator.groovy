package com.forufamily.gradle.plugin.tasks.aj.generator

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.builder.model.BuildType
import com.forufamily.gradle.plugin.tasks.aj.Callable
import org.apache.commons.io.FileUtils
import org.aspectj.org.eclipse.jdt.core.dom.AST
import org.aspectj.org.eclipse.jdt.core.dom.ASTParser
import org.aspectj.org.eclipse.jdt.core.dom.CompilationUnit
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.util.PatternSet

class JavaSourceGenerator implements Callable {
    private static final PatternSet PATTERN_SET = new PatternSet().include(["**/*.aj"])
    private static final String DIR_NAME_GEN = "build/generated/source/ajg/"
    private final JavaCompile javaCompile
    private final Project project
    private BuildType buildType
    private File target
    private List<Object> sourceSet

    JavaSourceGenerator(Project project, JavaCompile javaCompile, BuildType buildType) {
        this.javaCompile = javaCompile
        this.project = project
        this.buildType = buildType
        sourceSet = new ArrayList<>()

        // The generated java file will be put into "generated/source/ajg"
        target = new File(project.getProjectDir(), DIR_NAME_GEN + buildType.name)
        "Generator work path:${target.absolutePath}".info()
        if (target.exists()) FileUtils.deleteDirectory(target)
        target.mkdirs()
    }

    @Override
    void call() {
        // ASTParser.newParser(AST.JLS3)
        def parser = ASTParser.newParser(AST.JLS3)
        parser.setCompilerOptions(new HashMap<>())
        def visitor = new AjAstSourceFlattener()

        ajFiles().each {
            def file = new File(it)
            parser.setSource(file.text.toCharArray())
            def unit = parser.createAST(null) as CompilationUnit
            unit.accept(visitor)
            File output = write(file, visitor.result)
            if (null != output) {
                // 将文件添加到javaCompile的sources中
                javaCompile.source(output.absolutePath)
            }
        }
    }

    File write(File sourceFile, String content) {
        File sourcePath = sourceSet.find { sourceFile.absolutePath.startsWith(it.absolutePath) }
        if (null == sourcePath) {
            "Abort generation, can not find source path:$sourceFile".info()
            return null
        }

        String targetPath = sourceFile.absolutePath.replace(sourcePath.absolutePath, "").replace(".aj", ".java")
        def javaFile = new File(target, targetPath)
        if (!javaFile.getParentFile().exists()) javaFile.getParentFile().mkdirs()
        if (!javaFile.exists()) javaFile.createNewFile()

        "Generated java file:[${javaFile.absolutePath}]".info()
        javaFile.write(content, "utf-8")
        return javaFile
    }

    List<String> ajFiles() {
        def extension = findExtension()

        if (sourceSet.isEmpty()) {
            extension.sourceSets.each {
                it.java.srcDirs.each {
                    "Add to sourceSets:$it".info()
                    sourceSet << it
                }
            }
        }

        def list = project.files(sourceSet as Object[]).getAsFileTree().matching(PATTERN_SET)
                .toList()
                .stream()
                .map { it.absolutePath }
                .collect()
        "All .aj files:$list".info()
        return list
    }

    private BaseExtension findExtension() {
        project.plugins.hasPlugin(AppPlugin) ? project.extensions.getByType(AppExtension) : project.extensions.getByType(LibraryExtension)
    }
}
