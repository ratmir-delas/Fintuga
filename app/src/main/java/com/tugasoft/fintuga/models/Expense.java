package com.tugasoft.fintuga.models;

import java.io.Serializable;

public class Expense implements Serializable {
    private String id;
    private double amount = 0;
    private String categoryId;
    private String date;
    private String description;
    private String proofName = "";
    private String proofUri = "";
    private boolean isExpense;
    private String category;
    private String color;

    public Expense() {
    }

    public Expense(double amount, String description, boolean isExpense, String category, String color, String proofName, String proofUri) {
        this.amount = amount;
        this.description = description;
        this.isExpense = isExpense;
        this.proofName = proofName;
        this.proofUri = proofUri;
        this.category = category;
        this.color = color;
    }

    public Expense(String id, double amount, String description, boolean isExpense, String category, String color, String proofName, String proofUri) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.isExpense = isExpense;
        this.proofName = proofName;
        this.proofUri = proofUri;
        this.category = category;
        this.color = color;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String str) {
        this.categoryId = str;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isExpense() {
        return this.isExpense;
    }

    public void setIsExpense(boolean str) {
        this.isExpense = str;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProofUri() {
        return this.proofUri;
    }

    public void setProofUri(String str) {
        this.proofUri = str;
    }

    public String getProofName() {
        return this.proofName;
    }

    public void setProofName(String proofName) {
        this.proofName = proofName;
    }

    public void setExpense(boolean expense) {
        isExpense = expense;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
