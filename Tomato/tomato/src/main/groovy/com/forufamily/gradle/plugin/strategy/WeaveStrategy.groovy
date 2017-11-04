package com.forufamily.gradle.plugin.strategy

import com.forufamily.gradle.plugin.ajc.Worker

interface WeaveStrategy {
    void start()
    Worker worker()
}
