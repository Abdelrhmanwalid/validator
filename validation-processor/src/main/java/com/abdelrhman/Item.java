package com.abdelrhman;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

/**
 * Created by abdelrhman on 8/30/2017.
 */

public class Item {
    private String name;
    private Element element;
    private String packageName;
    private boolean isPublic;
    private Annotation annotation;

    public Item(String name, Element element, String packageName, boolean isPublic, Annotation annotation) {
        this.name = name;
        this.element = element;
        this.packageName = packageName;
        this.isPublic = isPublic;
        this.annotation = annotation;

    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public String getName() {
        return name;
    }

    public Element getElement() {
        return element;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isAccessible() {
        return isPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (!name.equals(item.name)) return false;
        if (!element.equals(item.element)) return false;
        if (!packageName.equals(item.packageName)) return false;
        return annotation.equals(item.annotation);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + element.hashCode();
        result = 31 * result + packageName.hashCode();
        result = 31 * result + annotation.hashCode();
        return result;
    }
}
