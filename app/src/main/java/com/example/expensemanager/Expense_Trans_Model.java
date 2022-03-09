package com.example.expensemanager;

public class Expense_Trans_Model {

    String place;
    double amount;
    String card;
    String date;

    public Expense_Trans_Model(String place, double amount, String card, String date) {

        this.place = place;
        this.amount = amount;
        this.card = card;
        this.date = date;
    }


    public String getPlace() {
        return place;
    }

    public double getAmount() {
        return amount;
    }

    public String getCard() {
        return card;
    }

    public String getDate() {
        return date;
    }
}
