package com.abdelrhman.validator;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;

import com.abdelrhman.validator.annotaions.Max;
import com.abdelrhman.validator.annotaions.Min;
import com.abdelrhman.validator.annotaions.NotEmpty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by abdelrhman on 8/24/2017.
 */

public class Validator {

    private Map<EditText, List<Annotation>> annotationMap = new HashMap<>();

    public Validator(Activity activity) {
        Field[] fields = activity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (EditText.class.equals(field.getType())) {
                List<Annotation> annotations = null;
                for (Annotation annotation : field.getDeclaredAnnotations()) {
                    boolean add = false;
                    if (annotation instanceof NotEmpty || annotation instanceof Max || annotation instanceof Min) {
                        add = true;
                        if (annotations == null) {
                            annotations = new ArrayList<>();
                        }
                        annotations.add(annotation);
                    }
                    if (add)
                        annotationMap.put(getEditTextFromField(field, activity), annotations);
                }
            }
        }
    }

    private boolean validateNotEmpty(EditText editText, NotEmpty notEmpty) {
        if (TextUtils.isEmpty(editText.getText())) {
            editText.setError(notEmpty.errorMessage());
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    private boolean validateMax(EditText editText, Max max) {
        boolean valid = editText.getText().length() <= max.value();
        editText.setError(valid ? null : max.errorMessage());
        return valid;
    }

    private boolean validateMin(EditText editText, Min min) {
        boolean valid = editText.getText().length() >= min.value();
        editText.setError(valid ? null : min.errorMessage());
        return valid;
    }

    public boolean validate() {
        boolean valid = true;
        Set<EditText> editTexts = annotationMap.keySet();
        for (EditText editText : editTexts) {
            List<Annotation> annotations = annotationMap.get(editText);
            for (Annotation annotation : annotations) {
                if (annotation instanceof NotEmpty) {
                    boolean isNotEmptyValid = validateNotEmpty(editText, ((NotEmpty) annotation));
                    valid = valid && isNotEmptyValid;
                    if (!isNotEmptyValid)
                        break;
                } else if (annotation instanceof Max) {
                    boolean isMaxValid = validateMax(editText, ((Max) annotation));
                    valid = valid && isMaxValid;
                    if (!isMaxValid)
                        break;
                } else if (annotation instanceof Min) {
                    boolean isMinValid = validateMin(editText, ((Min) annotation));
                    valid = valid && isMinValid;
                    if (!isMinValid)
                        break;
                }
            }
        }
        return valid;
    }

    private EditText getEditTextFromField(Field field, Activity activity) {
        field.setAccessible(true);
        try {
            return (EditText) field.get(activity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
