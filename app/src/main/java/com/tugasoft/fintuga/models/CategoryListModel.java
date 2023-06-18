package com.tugasoft.fintuga.models;

import java.util.ArrayList;

public class CategoryListModel {
    private ArrayList<Category> expenseCategoryList;
    private ArrayList<Category> incomeCategoryList;

    public ArrayList<Category> getIncomeCategoryList() {
        return this.incomeCategoryList;
    }

    public void setIncomeCategoryList(ArrayList<Category> arrayList) {
        this.incomeCategoryList = arrayList;
    }

    public ArrayList<Category> getExpenseCategoryList() {
        return this.expenseCategoryList;
    }

    public void setExpenseCategoryList(ArrayList<Category> arrayList) {
        this.expenseCategoryList = arrayList;
    }
}
