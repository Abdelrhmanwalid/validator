package com.abdelrhman.validator.annotaions;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * check if an {@link android.widget.EditText} has text with length > max
 * example
 * <pre>
 * {@code @Max(11) EditText editText;}
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Max {
    /**
     * @return max text length
     */
    int value();

    /**
     * @return error message to be displayed, defaults to "text is too long"
     */
    String errorMessage() default "text is too long";
}
