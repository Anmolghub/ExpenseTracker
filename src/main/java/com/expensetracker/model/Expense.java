package com.expensetracker.model;

public class Expense {
    private int id;
    private String date;
    private String category;
    private double amount;
    private String note;

    // Constructor with ID (for reading from DB)
    public Expense(int id, String date, String category, double amount, String note) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.note = note;
    }

    // Constructor without ID (for inserting new data)
    public Expense(String date, String category, double amount, String note) {
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.note = note;
    }

    // Getters
    public int getId() { return id; }
    public String getDate() { return date; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }

    // Setters (optional, in case you need them later)
    public void setId(int id) { this.id = id; }
    public void setDate(String date) { this.date = date; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setNote(String note) { this.note = note; }

    // For printing in console
    @Override
    public String toString() {
        return id + " | " + date + " | " + category + " | " + amount + " | " + note;
    }
}
