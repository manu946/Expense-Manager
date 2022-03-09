package com.example.expensemanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.expensemanager.Data.MyDbHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.expensemanager.App.CHANNEL_ID;

public class SmsListener extends BroadcastReceiver {

    private static MessageListener mListener;
    MyDbHandler myDbHandler;
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences sharedPreferences=context.getSharedPreferences("MAIN",Context.MODE_PRIVATE);
        boolean lng=sharedPreferences.getBoolean("NOTIFICATION",false);
        int limit=sharedPreferences.getInt("LIMIT",0);
        myDbHandler=new MyDbHandler(context);
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for(int i=0; i<pdus.length; i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String text=smsMessage.getMessageBody();
            long date=smsMessage.getTimestampMillis();
            String patternString = "בסך (\\d{0,10})";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(text);

            if (text.contains("מאסטרקארד") || text.contains("MasterCard") || text.contains("Mastercard") || text.contains("www.cal-online.co.il")) {
                if (matcher.find()) {
                    if (matcher.group(1) != null && !matcher.group(1).trim().isEmpty()) {
                        int from_index=text.indexOf("ב",text.indexOf("0912"));
                        int point_index=text.indexOf("בסך",text.indexOf("0912"));
                        if(from_index!=0 && point_index!=0){
                            if(point_index-from_index>0){
                                Date res = new Date(date);
                                DateFormat sdf1 = new SimpleDateFormat("YYYY-MM-dd");
                                myDbHandler.insertuserdata(text.substring(from_index,point_index), Double.parseDouble(matcher.group(1)), "Mastercard Ends With 0912", sdf1.format(res));
                            }
                        }

                    }

                }
            }
            else if (text.contains("Isracard") || text.contains("ישראכרט") || text.contains("isracard")) {

                if (text.contains("USD")) {
                    if (matcher.find()) {

                        if (matcher.group(1) != null && !matcher.group(1).trim().isEmpty()) {
                            int from_index=text.indexOf("ב",text.indexOf(matcher.group(1)));
                            int point_index=text.indexOf(".",text.indexOf(".")+1);
                            if(from_index!=0 && point_index!=0){
                                if(point_index-from_index>0){
                                    Date res = new Date(date);
                                    DateFormat sdf1 = new SimpleDateFormat("YYYY-MM-dd");
                                    myDbHandler.insertuserdata(text.substring(from_index,point_index), Double.parseDouble(matcher.group(1)) * 3.31,
                                            "Isracard Ends With 1223", sdf1.format(res));
                                }
                            }

                        }

                    }
                } else {
                    if (matcher.find()) {
                        int from_index=text.indexOf("ב",text.indexOf(matcher.group(1)));
                        int point_index=text.indexOf(".",text.indexOf(".")+1);
                        if(from_index!=0 && point_index!=0){
                            if(point_index-from_index>0){
                                Date res = new Date(date);
                                DateFormat sdf1 = new SimpleDateFormat("YYYY-MM-dd");
                                myDbHandler.insertuserdata(text.substring(from_index,point_index), Double.parseDouble(matcher.group(1)),
                                        "Isracard Ends With 1223", sdf1.format(res));
                            }
                        }
                    }
                }


            }

        }
        double dd = myDbHandler.all_amount();
        if(lng){
            if(limit<=dd){
                Intent notificationIntent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,
                        0, notificationIntent, 0);
                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("Alert")
                        .setContentText("Expense Extended")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentIntent(pendingIntent)
                        .build();
                mNotificationManager.notify(56, notification);

            }
        }

    }

}