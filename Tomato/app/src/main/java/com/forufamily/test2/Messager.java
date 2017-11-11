package com.forufamily.test2;

public class Messager {
    private static String name = "Hello";

    public void otherMessage() {
        call(() -> "message");
    }

    private void call(OnMessage o) {
        System.out.println(o.message());
    }

    @Inject
    public static void message() {
        System.out.println(name + ", 来自Messager的消息-, 如果注入切面失败， 你会看到1111------------------");
    }

    interface OnMessage {
        String message();
    }
}
