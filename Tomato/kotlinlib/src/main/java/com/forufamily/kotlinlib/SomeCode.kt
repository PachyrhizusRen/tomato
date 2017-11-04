package com.forufamily.kotlinlib

class SomeCode {
    val name: String = "KotlinLib"

    @KotlinLibInject
    override fun toString(): String {
        return name
    }
}