package com.forufamily.test2;

import android.os.Handler;

public class Messager {
    private static String name = "Hello";

    private static Handler handler = new Handler();

    public void otherMessage() {
        call(() -> "other--message11");
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
