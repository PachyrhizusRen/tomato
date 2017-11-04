package com.forufamily.kotlinlib

@org.aspectj.lang.annotation.Aspect
class KotlinLibInjectAspect {

    @Throws(Throwable::class)
    @org.aspectj.lang.annotation.Around(POINT_CUT_INJECT)
    fun weaveConstantsJointPoint(): Any? {
        println("方法拦截成功")
        return null
    }

    companion object {
        const val POINT_CUT_INJECT = "execution(@com.forufamily.kotlinlib.KotlinLibInject * *(..))"
    }
}