package com.abdelrhman.reflectionvalidator.annotaions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * check if an {@link android.widget.EditText} has text with length < min
 * example
 * <pre>
 * {@code @Min EditText editText;}
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Min {

    /**
     * @return min text length
     */
    int value();

    /**
     * @return error message to be displayed, defaults to "text is too short"
     */
    String errorMessage() default "text is too short";
}
