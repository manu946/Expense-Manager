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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final String myPackageName = getPackageName();
                    if (Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {

                      //  List<Sms> lst = getAllSms();
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

                        Toast.makeText(this, "Restart App To Fetch Your Expenses", Toast.LENGTH_SHORT).show();

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