package com.tugasoft.fintuga.models;

public class Category {
    private String id;
    private String category;
    private String color;

    public Category(String category, String color) {
        this.category = category;
        this.color = color;
    }

    public Category(String id, String category, String color) {
        this.id = id;
        this.category = category;
        this.color = color;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String str) {
        this.category = str;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String str) {
        this.color = str;
    }
}
