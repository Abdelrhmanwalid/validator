package com.abdelrhman.reflectionvalidator.annotaions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmpty {

    /**
     * @return error message to be displayed, defaults to an empty string
     */
    String errorMessage() default "can not be empty";

}
