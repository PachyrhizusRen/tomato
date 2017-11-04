package com.forufamily.chat.aspect;

/**
 * @author sharoncn
 *         Created by Administrator on 2017/10/31.
 */
@org.aspectj.lang.annotation.Aspect
public class JavaInjectAspect {
    private static final String POINT_CUT_INJECT = "execution(@com.forufamily.chat.aspect.JavaInject * *(..))";

    @org.aspectj.lang.annotation.Around(POINT_CUT_INJECT)
    public Object weaveConstantsJointPoint() {
        System.out.println("java inject 拦截00000000000000000");
        return null;
    }
}
