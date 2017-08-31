package com.abdelrhman;

import android.text.TextUtils;
import android.widget.EditText;

import com.abdelrhman.validator.annotaions.Max;
import com.abdelrhman.validator.annotaions.Min;
import com.abdelrhman.validator.annotaions.NotEmpty;
import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class ValidationProcessor extends AbstractProcessor {

    Set<Item> items = new HashSet<>();
    Set<TypeElement> typeElements = new HashSet<>();
    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;
    private Messager messager;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(NotEmpty.class.getCanonicalName());
        annotations.add(Min.class.getCanonicalName());
        annotations.add(Max.class.getCanonicalName());
        return annotations;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        processAnnotation(roundEnvironment, Min.class);
        processAnnotation(roundEnvironment, Max.class);
        processAnnotation(roundEnvironment, NotEmpty.class);

        for (TypeElement typeElement : typeElements) {
            genValidationClass(typeElement);
        }

        return false;
    }


    private boolean haveAccess(Item item) {
        if (!item.isAccessible())
            messager.printMessage(Diagnostic.Kind.ERROR,
                    String.format("%s in %s can NOT be private", item.getName(),
                            item.getElement().getEnclosingElement().toString()));
        return item.isAccessible();
    }

    private boolean verifyIsEditText(Item item) {
        boolean isEditText = EditText.class.getName().equals(item.getElement().asType().toString());
        if (!isEditText) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    String.format("%s in %s is not an instance of %s", item.getName(),
                            item.getElement().getEnclosingElement().toString(), EditText.class.getName()));
        }
        return isEditText;
    }

    private void genValidationClass(TypeElement typeElement) {

        TypeSpec.Builder builder = TypeSpec.classBuilder(typeElement.getSimpleName() + "_Validator")
                .addModifiers(Modifier.FINAL);
        List<MethodSpec> methodSpecs = new ArrayList<>();
        String typeElementName = typeElement.getSimpleName().toString();
        ClassName className = ClassName.get(elementUtils.getPackageOf(typeElement).toString(), typeElementName);
        String lowercaseName = typeElementName.substring(0, 1).toLowerCase() + typeElementName.substring(1);
        MethodSpec.Builder validationMethodBuilder = MethodSpec.methodBuilder("validate")
                .addModifiers(Modifier.STATIC)
                .returns(boolean.class)
                .addParameter(className,
                        lowercaseName);
        Set<Item> elementsByClass = getElementsByClass(typeElement);

        validationMethodBuilder.addStatement("$T valid = true", boolean.class);
        for (Item item : elementsByClass) {
            MethodSpec.Builder methodBuilder =
                    MethodSpec.methodBuilder("verify" + item.getName().substring(0, 1).toUpperCase() +
                            item.getName().substring(1) + item.getAnnotation().annotationType().getSimpleName())
                            .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                            .returns(boolean.class)
                            .addParameter(EditText.class, "editText");
            addCode(lowercaseName, item.getName(), methodBuilder, item.getAnnotation(), validationMethodBuilder);
            methodSpecs.add(methodBuilder.build());
        }
        validationMethodBuilder.addStatement("return $N", "valid");

        methodSpecs.add(validationMethodBuilder.build());

        builder.addMethods(methodSpecs);
        JavaFile javaFile =
                JavaFile.builder(elementUtils.getPackageOf(typeElement).toString(), builder.build())
                        .indent("    ")
                        .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addCode(String objectName, String fieldName, MethodSpec.Builder builder, Annotation annotation, MethodSpec.Builder validationMethodBuilder) {
        if (annotation instanceof NotEmpty) {
            addNotEmptyCode(builder);
            validationMethodBuilder.addStatement("valid = valid && $N($N.$N, $S)",
                    builder.build().name, objectName, fieldName, ((NotEmpty) annotation).errorMessage());
        } else if (annotation instanceof Min) {
            addMinCode(builder);
            validationMethodBuilder.addStatement("valid = valid && $N($N.$N, $S, $N)",
                    builder.build().name, objectName, fieldName,
                    ((Min) annotation).errorMessage(), String.valueOf(((Min) annotation).value()));
        } else if (annotation instanceof Max) {
            addMaxCode(builder);
            validationMethodBuilder.addStatement("valid = valid && $N($N.$N, $S, $N)",
                    builder.build().name, objectName, fieldName,
                    ((Max) annotation).errorMessage(), String.valueOf(((Max) annotation).value()));
        }
    }

    private void addMaxCode(MethodSpec.Builder builder) {
        builder.addParameter(String.class, "errorMessage")
                .addParameter(int.class, "value")
                .addStatement("boolean valid = $N.getText().length() <= $N", "editText", "value")
                .addStatement("$N.setError(valid ? null : $N)", "editText", "errorMessage")
                .addStatement("return valid");
    }

    private void addMinCode(MethodSpec.Builder builder) {
        builder.addParameter(String.class, "errorMessage")
                .addParameter(int.class, "value")
                .addStatement("boolean valid = $N.getText().length() >= $N", "editText", "value")
                .addStatement("$N.setError(valid ? null : $N)", "editText", "errorMessage")
                .addStatement("return valid");
    }

    private void addNotEmptyCode(MethodSpec.Builder builder) {
        builder.addParameter(String.class, "errorMessage")
                .beginControlFlow("if ($T.isEmpty($N.getText()))", TextUtils.class, "editText")
                .addStatement("$N.setError($N)", "editText", "errorMessage")
                .addStatement("return false")
                .nextControlFlow("else")
                .addStatement("$N.setError(null)", "editText")
                .addStatement("return true")
                .endControlFlow();
    }

    private void processAnnotation(RoundEnvironment roundEnvironment, Class<? extends Annotation> annotation) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(annotation);
        for (Element element : elements) {
            Item item = new Item(
                    element.getSimpleName().toString(),
                    element,
                    elementUtils.getPackageOf(element).toString(),
                    !MoreElements.hasModifiers(Modifier.PRIVATE).apply(element),
                    element.getAnnotation(annotation)
            );
            if (verifyIsEditText(item) && haveAccess(item))
                this.items.add(item);
            typeElements.add(MoreElements.asType(element.getEnclosingElement()));
        }
    }

    private Set<Item> getElementsByClass(TypeElement typeElement) {
        Set<Item> items = new HashSet<>();
        for (Item item : this.items) {
            if (typeElement.equals(item.getElement().getEnclosingElement()))
                items.add(item);
        }
        return items;
    }
}
