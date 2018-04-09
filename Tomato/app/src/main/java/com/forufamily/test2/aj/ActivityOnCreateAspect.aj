package com.forufamily.test2.aj;

import org.aspectj.lang.Signature;

public aspect ActivityOnCreateAspect {

    public pointcut onCreate() : call(void android.app.Activity+.setContentView(int));

    before() : onCreate(){
         Signature signature = thisJoinPoint.getSignature();
         System.out.println("调用" + signature.getName());
    }
}