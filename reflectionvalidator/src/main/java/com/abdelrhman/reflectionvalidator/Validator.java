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
                        field.setAccessible(true);
                        NotEmpty notEmpty = ((NotEmpty) annotation);
                        try {
                            EditText editText = (EditText) field.get(activity);
                            valid = validateNotEmpty(editText, notEmpty);
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

    private static boolean validateNotEmpty(EditText editText, NotEmpty notEmpty) {
        if (TextUtils.isEmpty(editText.getText())) {
            editText.setError(notEmpty.errorMessage());
            System.out.println("not valid");
            return false;
        } else {
            editText.setError(null);
            System.out.println("valid");
            return true;
        }
    }
}
