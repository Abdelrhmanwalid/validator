package com.abdelrhman.validator;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;

import com.abdelrhman.validator.annotaions.Max;
import com.abdelrhman.validator.annotaions.Min;
import com.abdelrhman.validator.annotaions.NotEmpty;

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
            if (EditText.class.equals(field.getType())) {
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    System.out.println(annotation.toString());
                    if (annotation instanceof NotEmpty) {
                        EditText editText = getEditTextFromField(field, activity);
                        boolean isNotEmptyValid = validateNotEmpty(editText, ((NotEmpty) annotation));
                        valid = valid && isNotEmptyValid;
                        if (!isNotEmptyValid)
                            break;
                    } else if (annotation instanceof Max) {
                        EditText editText = getEditTextFromField(field, activity);
                        boolean isMaxValid = validateMax(editText, ((Max) annotation));
                        valid = valid && isMaxValid;
                        if (!isMaxValid)
                            break;
                    } else if (annotation instanceof Min) {
                        EditText editText = getEditTextFromField(field, activity);
                        boolean isMinValid = validateMin(editText, ((Min) annotation));
                        valid = valid && isMinValid;
                        if (!isMinValid)
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
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    private static boolean validateMax(EditText editText, Max max) {
        boolean valid = editText.getText().length() <= max.value();
        editText.setError(valid ? null : max.errorMessage());
        return valid;
    }

    private static boolean validateMin(EditText editText, Min min) {
        boolean valid = editText.getText().length() >= min.value();
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
