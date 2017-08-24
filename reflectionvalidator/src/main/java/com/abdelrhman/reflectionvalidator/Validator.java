package com.abdelrhman.reflectionvalidator;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;

import com.abdelrhman.reflectionvalidator.annotaions.NotEmpty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by abdelrhman on 8/24/2017.
 */

public class Validator {
    public static boolean validate(Activity activity) {
        boolean valid = true;
        Field[] fields = activity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(NotEmpty.class) && EditText.class.equals(field.getType())) {
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    if (annotation instanceof NotEmpty) {
                        NotEmpty notEmpty = ((NotEmpty) annotation);
                        field.setAccessible(true);
                        try {
                            EditText editText = (EditText) field.get(activity);
                            if (TextUtils.isEmpty(editText.getText())) {
                                valid = false;
                                editText.setError(notEmpty.errorMessage());
                                System.out.println("not valid");
                            } else {
                                editText.setError(null);
                                System.out.println("valid");
                            }

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
        return valid;
    }
}
