package com.abdelrhman.reflectionvalidator.annotaions;

/**
 * check if an {@link android.widget.EditText} has text with length < min
 * example
 * <pre>
 * {@code @Min EditText editText;}
 * </pre>
 */
public @interface Min {

    /**
     * @return min text length
     */
    int length();

    /**
     * @return error message to be displayed, defaults to "text is too long"
     */
    String errorMessage() default "text is too short";
}
