package com.zsy.blog.annos;

import com.zsy.blog.common.Constants;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 郑书宇
 * @create 2023/1/18 11:25
 * @desc
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Delay {

    long time() default Constants.TIME;

    int  count() default Constants.COUNT;

    String type() default "";

}
