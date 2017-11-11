package com.forufamily.test2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.forufamily.chat.Sender
import com.forufamily.lib.Hello

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setup()
        message()
        message1()

        println("调用Messager， 它是Java类--修改测试InstantRun")
        Messager.message()

        Messager().otherMessage()

        val desk = Sender()
        println(desk)

        val hello = Hello()
        hello.hello()
        Hello.sayHello()
    }

    @Inject
    private fun setup() {
        println("方法注入失败会看到这个提示---111-2111122--- 1111  ==")
    }

    @Inject
    private fun message() {
        println("测试增量，如果未注入成功则可以看到这个消息222222-----")
    }

    @Inject
    private fun message1() {
        println("测试增量，如果未注入成功则可以看到这个消息-----")
    }
}
