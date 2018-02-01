package com.forufamily.gradle.plugin.strategy

import aj.org.objectweb.asm.Opcodes
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformInvocation
import com.forufamily.gradle.plugin.ajc.Worker
import com.forufamily.gradle.plugin.asm.AspectVisitor
import com.forufamily.gradle.plugin.strategy.impl.IncrementalWeaveStrategy
import com.forufamily.gradle.plugin.strategy.impl.NoIncrementalWeaveStrategy
import com.google.common.io.Closer
import org.objectweb.asm.ClassReader

import java.util.jar.JarEntry
import java.util.jar.JarFile

class WeaveStrategyFactory {

    static WeaveStrategy create(Worker worker, TransformInvocation invocation, List<String> excludedJars) {
        def inputs = invocation.inputs
        def provider = invocation.outputProvider
        return invocation.incremental && !aspectChanged(invocation) ?
                new IncrementalWeaveStrategy(worker, inputs, provider, excludedJars) :
                new NoIncrementalWeaveStrategy(worker, inputs, provider, excludedJars)
    }

    // 增量转换时，分析切面是否发生变化。如果切面发生变化，则直接进行非增量织入。
    // 这里的分析粒度很大(仅分析切面)，目前暂时无法实现精细的分析切点。不过既然aspectj能够织入，我们使用相同的方案应该也能够完成这个工作。
    // 可以作为后期扩展方向
    private static boolean aspectChanged(TransformInvocation invocation) {
        def result = invocation.inputs.find {
            return it.directoryInputs.find { dir ->
                // 查找变化的目录中是否存在变化的切面class
                def result = dir.changedFiles.find { file, status -> care(status) && hasAspect(file) }
                return null != result
            } || it.jarInputs.find { jar ->
                if (care(jar.status)) {
                    def closer = Closer.create()
                    // 查找变化的jar文件中是否存在切面class
                    try {
                        JarFile jarFile = closer.register(new JarFile(jar.file))
                        def result = jarFile.entries().find { entry -> hasAspect(entry, jarFile) }
                        return null != result
                    } catch (Throwable e) {
                        closer.rethrow(e)
                    } finally {
                        closer.close()
                    }
                }
                return false
            }
        }
        if (result) "发现切面资源发生变化, 禁用增量转换".info()
        //invocation.debugFileStatus()
        return result
    }

    // 只关注Added和Changed两种状态
    private static boolean care(Status status) {
        return Status.ADDED == status || Status.CHANGED == status
    }

    // 分析JarEntry的class中是否有Aspect注解存在(由于确定jar中Class变化的逻辑比较复杂, 这里使用的粒度为整个jar)
    private static boolean hasAspect(JarEntry entry, JarFile jarFile) {
        // jar中可能存在非class文件(如: .properties, .MF)
        if (entry.name.toLowerCase().endsWith(".class")) {
            return hasAspect(jarFile.getInputStream(entry).bytes)
        }
        return false
    }

    // 分析class文件是否有Aspect注解存在
    private static boolean hasAspect(File classFile) {
        try {
            return classFile.exists() && !classFile.isDirectory() && hasAspect(classFile.bytes)
        } catch (Exception ignored){}
        return false
    }

    private static boolean hasAspect(byte[] bytes) {
        def reader = new ClassReader(bytes)
        def visitor = new AspectVisitor(Opcodes.ASM4)
        reader.accept(visitor, Opcodes.ASM4)
        return visitor.hasAspect
    }
}
