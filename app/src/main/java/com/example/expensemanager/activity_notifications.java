package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

public class activity_notifications extends AppCompatActivity {

    Switch aSwitch;
    EditText editText;
    ImageView tick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        aSwitch=findViewById(R.id.switc);
        editText=findViewById(R.id.limit);
        tick=findViewById(R.id.tick);
        SharedPreferences sharedPreferences=getSharedPreferences("MAIN",Context.MODE_PRIVATE);
        boolean lng=sharedPreferences.getBoolean("NOTIFICATION",false);
        int limit=sharedPreferences.getInt("LIMIT",0);
        if(lng){
            aSwitch.setChecked(true);
            editText.setVisibility(View.VISIBLE);
            editText.setText(Integer.toString(limit));
        }
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editText.setVisibility(View.VISIBLE);
                }
                else {
                    editText.setVisibility(View.GONE);
                }
            }
        });
        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(aSwitch.isChecked()){
                    if(!editText.getText().toString().trim().isEmpty()){
                        SharedPreferences.Editor editor = getSharedPreferences("MAIN", MODE_PRIVATE).edit();
                        editor.putBoolean("NOTIFICATION",true);
                        editor.putInt("LIMIT", Integer.parseInt(editText.getText().toString()));
                        editor.apply();
                        Toast.makeText(activity_notifications.this, "Setting Updated", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    SharedPreferences.Editor editor = getSharedPreferences("MAIN", MODE_PRIVATE).edit();
                    editor.putBoolean("NOTIFICATION",false);
                    editor.putInt("LIMIT", 0);
                    editor.apply();
                    Toast.makeText(activity_notifications.this, "Setting Updated", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}