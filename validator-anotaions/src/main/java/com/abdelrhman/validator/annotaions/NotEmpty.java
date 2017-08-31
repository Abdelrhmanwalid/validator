package com.abdelrhman.validator.annotaions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * check if an {@link android.widget.EditText} has empty text
 * example
 * <pre>
 * {@code @NotEmpty EditText editText;}
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface NotEmpty {

    /**
     * @return error message to be displayed, defaults to "can not be empty"
     */
    String errorMessage() default "can not be empty";

}
