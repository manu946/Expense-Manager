package com.example.expensemanager.Params;

public class Params {

    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "EXPENSES_DATABASE";
    public static final String TABLE_NAME = "EXPENSE";


    //Keys of our table in db
    public static final String KEY_ID = "id";
    public static final String KEY_PLACE="place";
    public static final String KEY_AMOUNT="amount"; //"0" for have not read sms and "1" for have read sms
    public static final String KEY_DATE="date";
    public static final String KEY_CARD_NUMBER="card";
}

