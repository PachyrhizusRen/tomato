package com.forufamily.gradle.plugin.tasks.aj.compiler

import org.aspectj.ajde.core.IBuildMessageHandler
import org.aspectj.bridge.AbortException
import org.aspectj.bridge.IMessage

class AjBuildMessageHandler implements IBuildMessageHandler {

    @Override
    boolean handleMessage(IMessage message) throws AbortException {
        "AjBuildMessage:${message}".info()
        return true
    }

    @Override
    boolean isIgnoring(IMessage.Kind kind) {
        return false
    }

    @Override
    void dontIgnore(IMessage.Kind kind) {

    }

    @Override
    void ignore(IMessage.Kind kind) {

    }
}
