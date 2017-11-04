package com.forufamily.test2;

public class Messager {
    private static String name = "Hello";

    @Inject
    public static void message() {
        System.out.println(name + ", 来自Messager的消息-, 如果注入切面失败， 你会看到1111------------------");
    }
}
