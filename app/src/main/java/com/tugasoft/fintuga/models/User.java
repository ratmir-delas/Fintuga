package com.tugasoft.fintuga.models;

public class User {
    public String id;
    private String categories;
    private String currencySymbol;
    private String fullName;
    private boolean isAdminUser = false;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCurrencySymbol() {
        return this.currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCategories() {
        return this.categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public boolean isAdminUser() {
        return this.isAdminUser;
    }

    public void setAdminUser(boolean adminUser) {
        this.isAdminUser = adminUser;
    }
}
