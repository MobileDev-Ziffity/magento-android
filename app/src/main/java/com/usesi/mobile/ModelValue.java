package com.usesi.mobile;

/**
 * Created by selva on 22/2/18.
 */

public class ModelValue {

    private String label, value, path;

    public ModelValue(String label, String value, String path) {
        this.label = label;
        this.value = value;
        this.path = path;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
