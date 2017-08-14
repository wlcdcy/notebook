package com.example.office;

public enum ContentType {
    TEXT(1, "text"), IMAGE(2, "image"),FOOTNOTEREF(3,"footnote");
    private int value;
    private String name;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private ContentType(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
