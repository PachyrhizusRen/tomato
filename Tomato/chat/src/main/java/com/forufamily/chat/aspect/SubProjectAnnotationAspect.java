package com.forufamily.chat.aspect;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class SubProjectAnnotationAspect {

    @Around("execution(@com.forufamily.common.DoInject * com..*.SubProjectAnnotationTest.doSomething(..))")
    public void weave() {
        System.out.println("SubProject annotation test is success!");
    }

}
