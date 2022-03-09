package com.example.expensemanager.Data;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.expensemanager.Expense_Trans_Model;
import com.example.expensemanager.Params.Params;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyDbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = Params.DB_NAME;
    public MyDbHandler(Context context) {
        super(context, "expns.db", null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase DB) {

        DB.execSQL("create Table EXPENSE(id INTEGER PRIMARY KEY AUTOINCREMENT, place TEXT, amount INTEGER,date TEXT,card TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int ii) {
        // Drop older table if existed
        DB.execSQL("DROP TABLE IF EXISTS " + Params.TABLE_NAME);

        // Create tables again
        onCreate(DB);
    }
    public Boolean insertuserdata(String place,double amount,String card,String date)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Params.KEY_PLACE, place);
        contentValues.put(Params.KEY_AMOUNT, amount);
        contentValues.put(Params.KEY_CARD_NUMBER, card);
        contentValues.put(Params.KEY_DATE,date);
        long result=DB.insert("EXPENSE", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }
    @SuppressLint("Range")
    public double all_amount(){
        double total=0;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + "amount" + ") as ttl FROM " +"EXPENSE", null);
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndex("ttl"));// get final total
        }
        cursor.close();
        db.close();
        return total;
    }
    public void delete_all(){
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.execSQL("delete from EXPENSE");
    }
    public Boolean updateuserdata(String name, String contact, String dob)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("contact", contact);
        contentValues.put("dob", dob);
        Cursor cursor = DB.rawQuery("Select * from Userdetails where name = ?", new String[]{name});
        if (cursor.getCount() > 0) {
            long result = DB.update("Userdetails", contentValues, "name=?", new String[]{name});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    public Boolean deletedata (String name)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Userdetails where name = ?", new String[]{name});
        if (cursor.getCount() > 0) {
            long result = DB.delete("Userdetails", "name=?", new String[]{name});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    @SuppressLint("Range")
    public List<Expense_Trans_Model> getallExpense(){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Params.TABLE_NAME, null, null, null, null, null, null, null);

            if(cursor!=null)
                if(cursor.moveToFirst()){
                    List<Expense_Trans_Model> studentList = new ArrayList<>();
                    do {

                        String place = cursor.getString(cursor.getColumnIndex(Params.KEY_PLACE));
                        double amount = cursor.getDouble(cursor.getColumnIndex(Params.KEY_AMOUNT));
                        String card = cursor.getString(cursor.getColumnIndex(Params.KEY_CARD_NUMBER));
                        String date = cursor.getString(cursor.getColumnIndex(Params.KEY_DATE));

                        studentList.add(new Expense_Trans_Model(place,amount,card,date));
                    }   while (cursor.moveToNext());

                    return studentList;
                }
        } catch (Exception e){

        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }
    @SuppressLint("Range")
    public List<Expense_Trans_Model> get_bw_dates(String start,String end){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Expense_Trans_Model> studentList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + Params.TABLE_NAME + " where date BETWEEN '" + start + "' AND '" + end + "'", null);

        if (cursor != null ) {
            if  (cursor.moveToFirst()) {
                do {

                    String place = cursor.getString(cursor.getColumnIndex(Params.KEY_PLACE));
                    double amount = cursor.getDouble(cursor.getColumnIndex(Params.KEY_AMOUNT));
                    String card = cursor.getString(cursor.getColumnIndex(Params.KEY_CARD_NUMBER));
                    String date = cursor.getString(cursor.getColumnIndex(Params.KEY_DATE));

                    studentList.add(new Expense_Trans_Model(place, amount, card, date));
                }while (cursor.moveToNext());
            }
        }
        return studentList;

    }
    @SuppressLint("Range")
    public double particular_month(String month,String year){
     // replace '05' with your month
        double total=0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT strftime('%m', date) as valMonth,SUM(amount) as valTotalDay FROM EXPENSE WHERE strftime('%Y', date)= '"+year+"' AND strftime('%m', date) = '"+month+"' GROUP BY valMonth", null);
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndex("valTotalDay"));// get final total
        }
        cursor.close();
        db.close();
        return total;
    }
}