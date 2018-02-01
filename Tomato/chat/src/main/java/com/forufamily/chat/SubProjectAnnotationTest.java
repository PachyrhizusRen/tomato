package com.forufamily.chat;

import com.forufamily.common.DoInject;

public class SubProjectAnnotationTest {

    @DoInject
    void doSomething() {
        System.out.println("I do not know what i suppose to do, if the injection failed you can see this.");
    }

    void ok() {
        System.out.println("aa111111111222222222211");
    }
}
