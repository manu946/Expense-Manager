package com.example.expensemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.expensemanager.Data.MyDbHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class settin_page extends AppCompatActivity {

    RelativeLayout rescan,reset,notif,month_per,about,cur;
    String[] cure={"$ USD","₪ Israel Currency"};
    int selectd_pos=-1;
    MyDbHandler myDbHandler;
    Cursor c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settin_page);
        rescan=findViewById(R.id.rescan);
        reset=findViewById(R.id.reset);
        cur=findViewById(R.id.currency);
        notif=findViewById(R.id.notif);
        month_per=findViewById(R.id.month_per);
        about=findViewById(R.id.about);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 myDbHandler=new MyDbHandler(settin_page.this);
                myDbHandler.delete_all();
                Toast.makeText(settin_page.this, "All Expense Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDbHandler=new MyDbHandler(settin_page.this);
                myDbHandler.delete_all();
                getAllSms();
                Toast.makeText(settin_page.this, "Rescan Done", Toast.LENGTH_SHORT).show();
            }
        });
        cur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(settin_page.this)
                        .setSingleChoiceItems(cure, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               selectd_pos=i;
                            }
                        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(selectd_pos==0){
                            SharedPreferences.Editor editor = getSharedPreferences("MAIN", MODE_PRIVATE).edit();
                            editor.putBoolean("IS_USD",true);
                            editor.apply();
                            Toast.makeText(settin_page.this, "Currency Updated.Restart App", Toast.LENGTH_SHORT).show();
                        }
                        else if(selectd_pos==1){
                            SharedPreferences.Editor editor = getSharedPreferences("MAIN", MODE_PRIVATE).edit();
                            editor.putBoolean("IS_USD",false);
                            editor.apply();
                            Toast.makeText(settin_page.this, "Currency Updated.Restart App", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(settin_page.this, "Please Select A Currency", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                        .show();
            }
        });

        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(settin_page.this,activity_notifications.class));
            }
        });
    }
    @SuppressLint("Range")
    public List<Sms> getAllSms() {
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getContentResolver();

        c = cr.query(message, null, null, null, null);
        startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {


                String text = c.getString(c.getColumnIndexOrThrow("body"));

                String patternString = "בסך (\\d{0,10})";
                String patternString2= "\\w+[ה]+(?= |$)";
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(text);
                Pattern pattern2 = Pattern.compile(patternString2);
                Matcher matcher1=pattern2.matcher(text);
                Log.d("Is-Find",Boolean.toString(matcher1.find()));
                if(text.contains("מאסטרקארד") || text.contains("MasterCard") || text.contains("Mastercard")|| text.contains("www.cal-online.co.il")){
                    if (matcher.find())
                    {
                        Date res = new Date(c.getLong(c.getColumnIndexOrThrow("date")));
                        DateFormat sdf1 = new SimpleDateFormat("YYYY-MM-dd");
                        myDbHandler.insertuserdata("La Case De Papel", Double.parseDouble(matcher.group(1)),"Mastercard Ends With 0912",sdf1.format(res));
                    }
                }
                else if( text.contains("Isracard") || text.contains("ישראכרט")|| text.contains("isracard")){

                    if(text.contains("USD")){
                        if (matcher.find())
                        {
                            Date res = new Date(c.getLong(c.getColumnIndexOrThrow("date")));
                            DateFormat sdf1 = new SimpleDateFormat("YYYY-MM-dd");
                            myDbHandler.insertuserdata("La Case De Papel", Double.parseDouble(matcher.group(1))*3.31,"Isracard Ends With 1223",sdf1.format(res));
                        }
                    }
                    else {
                        if (matcher.find())
                        {
                            Date res = new Date(c.getLong(c.getColumnIndexOrThrow("date")));
                            DateFormat sdf1 = new SimpleDateFormat("YYYY-MM-dd");
                            myDbHandler.insertuserdata("La Case De Papel", Double.parseDouble(matcher.group(1)),"Isracard Ends With 1223",sdf1.format(res));
                        }
                    }


                }

                lstSms.add(objSms);

                c.moveToNext();
            }
        }
        else {
            Toast.makeText(settin_page.this, "You Have No SMS", Toast.LENGTH_SHORT).show();
        }
        c.close();
        return lstSms;
    }
}