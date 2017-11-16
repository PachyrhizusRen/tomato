package com.forufamily.gradle.plugin.tasks.aj.compiler

import org.aspectj.ajde.core.IBuildProgressMonitor

class AjBuildProgressMonitor implements IBuildProgressMonitor {

    @Override
    void begin() {
        "开始编译".inof()
    }

    @Override
    void setProgressText(String text) {
        text.info()
    }

    @Override
    void finish(boolean wasFullBuild) {
        "编译结束:${wasFullBuild}".info()
    }

    @Override
    void setProgress(double percentDone) {
        "------------${percentDone}%------------".info()
    }

    @Override
    boolean isCancelRequested() {
        return false
    }
}
