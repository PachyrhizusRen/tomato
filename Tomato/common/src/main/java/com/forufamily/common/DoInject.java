package com.forufamily.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sharoncn
 *         Created by Administrator on 2017/11/23.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface DoInject {
}
