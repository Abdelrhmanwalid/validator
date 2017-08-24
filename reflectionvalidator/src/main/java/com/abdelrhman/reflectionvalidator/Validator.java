package com.abdelrhman.reflectionvalidator;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;

import com.abdelrhman.reflectionvalidator.annotaions.Max;
import com.abdelrhman.reflectionvalidator.annotaions.Min;
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
                        EditText editText = getEditTextFromField(field, activity);
                        valid = validateNotEmpty(editText, notEmpty);
                    } else if (annotation instanceof Max) {
                        EditText editText = getEditTextFromField(field, activity);
                        valid = valid && validateMax(editText, ((Max) annotation));
                    } else if (annotation instanceof Min) {
                        EditText editText = getEditTextFromField(field, activity);
                        valid = valid && validateMin(editText, ((Min) annotation));
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

    private static boolean validateMax(EditText editText, Max max) {
        boolean valid = editText.getText().length() > max.value();
        editText.setError(valid ? null : max.errorMessage());
        return valid;
    }

    private static boolean validateMin(EditText editText, Min min) {
        boolean valid = editText.getText().length() < min.value();
        editText.setError(valid ? null : min.errorMessage());
        return valid;
    }

    private static EditText getEditTextFromField(Field field, Activity activity) {
        field.setAccessible(true);
        try {
            return (EditText) field.get(activity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
