package in.usesi.mobile;

import com.google.gson.annotations.SerializedName;


public class Values {

        @SerializedName("Value")
        private String Value;

        @SerializedName("Path")
        private String path;

    public Values(String value, String path, String label) {
        Value = value;
        this.path = path;
        this.label = label;
    }

    @SerializedName("Label")

        private String label;

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
