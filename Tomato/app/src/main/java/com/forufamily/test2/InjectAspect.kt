package com.forufamily.test2

@org.aspectj.lang.annotation.Aspect
class InjectAspect {

    @Throws(Throwable::class)
    @org.aspectj.lang.annotation.Around(POINT_CUT_INJECT)
    fun weaveConstantsJointPoint(): Any? {
        println("方法拦截成功---2222---222----0000000000")
        return null
    }

    @Throws(Throwable::class)
    @org.aspectj.lang.annotation.Around("execution(* com.forufamily.lib.Hello.hello(..))")
    fun weaveHello(): Any? {
        println("方法Hello类hello方法拦截成功")
        return null
    }

    @Throws(Throwable::class)
    @org.aspectj.lang.annotation.Around("execution(* com.forufamily.lib.Hello.sayHello(..))")
    fun weaveStaticHello(): Any? {
        println("方法Hello类sayHello方法拦截成功")
        return null
    }

    companion object {
        const val POINT_CUT_INJECT = "execution(@com.forufamily.test2.Inject * *(..))"
    }
}