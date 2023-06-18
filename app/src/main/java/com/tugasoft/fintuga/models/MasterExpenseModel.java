package com.tugasoft.fintuga.models;

import java.io.Serializable;
import java.util.ArrayList;

public class MasterExpenseModel implements Serializable {
    private String date;
    private int Day;
    private ArrayList<Expense> expenses = new ArrayList<>();
    private boolean isHavingProofImage = false;
    private boolean isTransactionAdded = false;
    private double totalExpense = 0;
    private double totalIncome = 0;
    private String id;

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalIncome() {
        return this.totalIncome;
    }

    public void setTotalIncome(double j) {
        this.totalIncome = j;
    }

    public double getTotalExpense() {
        return this.totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public ArrayList<Expense> getExpenses() {
        return this.expenses;
    }

    public void setExpenses(ArrayList<Expense> expenses) {
        this.expenses = expenses;
    }

    public int getDay() {
        return this.Day;
    }

    public void setDay(int day) {
        this.Day = day;
    }

    public boolean isTransactionAdded() {
        return this.isTransactionAdded;
    }

    public void setTransactionAdded(boolean z) {
        this.isTransactionAdded = z;
    }

    public boolean isHavingProofImage() {
        return this.isHavingProofImage;
    }

    public void setHavingProofImage(boolean z) {
        this.isHavingProofImage = z;
    }
}
