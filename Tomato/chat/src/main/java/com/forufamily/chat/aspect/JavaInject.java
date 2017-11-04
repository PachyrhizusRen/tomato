package com.forufamily.chat.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sharoncn
 *         Created by Administrator on 2017/10/31.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface JavaInject {
}
