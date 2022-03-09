package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.Data.MyDbHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    TextView textView;
    BottomNavigationView bottomNavigationView;
    Fragment fragment;
    public static FragmentManager fragmentManager;
    MyDbHandler myDbHandler;
    ImageView imageView;
    Cursor c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        bottomNavigationView=findViewById(R.id.navigation);
        imageView=findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,settin_page.class));
            }
        });
        myDbHandler=new MyDbHandler(this);
        myDbHandler.delete_all();

        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);

        if (checkSelfPermission(Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS},
                    108);

        }
        else {
         //   List<Sms> lst = getAllSms();
        }
        fragment = new home_fragment();
        fragmentManager.beginTransaction().replace(R.id.framelayout, fragment).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        fragment = new home_fragment();

                        break;

                    case R.id.navigation_dashboard:

                        fragment = new chart_fragment();

                        break;


                }
                try {

                    fragmentManager.beginTransaction().replace(R.id.framelayout, fragment).commit();
                } catch (NullPointerException ff) {
                    ff.printStackTrace();
                }
                return true;
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
            Toast.makeText(MainActivity.this, "You Have No SMS", Toast.LENGTH_SHORT).show();
         }

        return lstSms;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final String myPackageName = getPackageName();
                    if (Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {

                        List<Sms> lst = getAllSms();
                    }
                }
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 108: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        final String myPackageName = getPackageName();
                        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {

                            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
                            startActivityForResult(intent, 1);
                        }else {
                            List<Sms> lst = getAllSms();
                            fragment = new home_fragment();
                            fragmentManager.beginTransaction().replace(R.id.framelayout, fragment).commit();
                        }
                    }else {
                        List<Sms> lst = getAllSms();
                        fragment = new home_fragment();
                        fragmentManager.beginTransaction().replace(R.id.framelayout, fragment).commit();
                    }

                } else {

                    Toast.makeText(this, "Give Permission Otherwise We Not Fetch Your Expenses", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void finish() {
        super.finish();
        Intent serviceIntent = new Intent(this, ExampleService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
}