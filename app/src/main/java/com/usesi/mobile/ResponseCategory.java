package com.usesi.mobile;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ResponseCategory {

    @SerializedName("Values")
    private List<Values> values;

    public List<Values> getValues() {
        return values;
    }

    public void setValues(List<Values> values) {
        this.values = values;
    }
}
