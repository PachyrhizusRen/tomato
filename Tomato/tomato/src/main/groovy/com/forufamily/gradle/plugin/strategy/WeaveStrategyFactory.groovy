package com.forufamily.gradle.plugin.strategy

import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformInvocation
import com.forufamily.gradle.plugin.ajc.Worker
import com.forufamily.gradle.plugin.strategy.impl.IncrementalWeaveStrategy
import com.forufamily.gradle.plugin.strategy.impl.NoIncrementalWeaveStrategy

import java.util.jar.JarEntry
import java.util.jar.JarFile

class WeaveStrategyFactory {

    static WeaveStrategy create(Worker worker, TransformInvocation invocation, List<String> excludedJars) {
        def inputs = invocation.inputs
        def provider = invocation.outputProvider
        return invocation.incremental && !aspectChanged(invocation, collectClassPath(invocation, worker.bootClassPath)) ?
                new IncrementalWeaveStrategy(worker, inputs, provider, excludedJars) :
                new NoIncrementalWeaveStrategy(worker, inputs, provider, excludedJars)
    }

    private static Collection<File> collectClassPath(TransformInvocation invocation, String bootClassPath) {
        def classPath = new ArrayList<File>()
        // 需要所有的文件都在classPath中，确保在解析类时不会出现NoClassFoundException
        invocation.inputs.each {
            it.directoryInputs.each { dir ->
                classPath << dir.file
            }

            it.jarInputs.each { jar ->
                classPath << jar.file
            }
        }

        invocation.referencedInputs.each {
            it.directoryInputs.each { dir ->
                classPath << dir.file
            }

            it.jarInputs.each { jar ->
                classPath << jar.file
            }
        }

        // 需要将bootClassPath放入到classPath中，防止在分析Class时找不到android.app.Activity等Class
        bootClassPath?.split(File.pathSeparator)?.each {
            classPath << it.toFile()
        }
        return classPath
    }

    // 增量转换时，分析切面是否发生变化。如果切面发生变化，则直接进行非增量织入。
    // 这里的分析粒度很大(仅分析切面)，目前暂时无法实现精细的分析切点。不过既然aspectj能够织入，我们使用相同的方案应该也能够完成这个工作。
    // 可以作为后期扩展方向
    private static boolean aspectChanged(TransformInvocation invocation, Collection<File> classPath) {
        def urls = classPath.stream()
                .map { it.toURI().toURL() }
                .collect()
                .toArray(new URL[classPath.size()])
        def loader = URLClassLoader.newInstance(urls, ClassLoader.getSystemClassLoader())
        def result = invocation.inputs.find {
            return it.directoryInputs.find { dir ->
                // 查找变化的目录中是否存在变化的切面class
                def result = dir.changedFiles
                        .find { file, status -> return care(status) && hasAspect(file, dir.file, loader) }
                return result
            } || it.jarInputs.find { jar ->
                if (care(jar.status)) {
                    // 查找变化的jar文件中是否存在切面class
                    JarFile jarFile = new JarFile(jar.file)

                    def result = jarFile.entries().find { entry ->
                        return hasAspect(entry, loader)
                    }
                    return result
                }
                return false
            }
        }
        loader.close()
        if (result) "发现切面资源发生变化, 禁用增量转换".info()
        return result
    }

    // 只关注Added和Changed两种状态
    private static boolean care(Status status) {
        return Status.ADDED == status || Status.CHANGED == status
    }

    // 分析JarEntry的class中是否有Aspect注解存在(由于确定jar中Class变化的逻辑比较复杂, 这里使用的粒度为整个jar)
    private static boolean hasAspect(JarEntry entry, ClassLoader loader) {
        // jar中可能存在非class文件(如: .properties, .MF)
        if (entry.name.toLowerCase().endsWith(".class")) {
            def className = entry.name.replace(".class", "").replace("/", ".")
            return hasAnnotation(loader, className)
        }
        return false
    }

    // 分析class文件是否有Aspect注解存在
    private static boolean hasAspect(File file, File dir, ClassLoader loader) {
        def className = file.absolutePath.replace(dir.absolutePath, "").replace(".class", "").replace(File.separator, ".")
        className = className.startsWith(".") ? className.substring(1) : className
        return hasAnnotation(loader, className)
    }

    // 根据类名称判断对应的类中是否存在Aspect注解
    private static boolean hasAnnotation(ClassLoader loader, String className) {
        def clazz = loader.loadClass(className)
        def aspect = loader.loadClass("org.aspectj.lang.annotation.Aspect")
        "类[${clazz.simpleName}]中是否有Aspect注解:${clazz.isAnnotationPresent(aspect)}".info()
        return clazz.isAnnotationPresent(aspect)
    }
}
