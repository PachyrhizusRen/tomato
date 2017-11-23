package com.forufamily.gradle.plugin.asm

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor

class AspectVisitor extends ClassVisitor {
    private static def ANNOTATION = "Lorg/aspectj/lang/annotation/Aspect;"
    def hasAspect = false

    AspectVisitor(int level) {
        super(level)
    }

    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if(ANNOTATION == desc) hasAspect = true
        return super.visitAnnotation(desc, visible)
    }
}
