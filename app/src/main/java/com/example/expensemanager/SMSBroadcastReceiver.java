package com.example.expensemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.expensemanager.Data.MyDbHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSBroadcastReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSBroadcastReceiver";
    MyDbHandler myDbHandler;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Bla Bla Bla Bla " + intent.getAction());
       myDbHandler=new MyDbHandler(context);
        if (intent.getAction() == SMS_RECEIVED) {

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }
                if (messages.length > -1) {

                    String text=messages[0].getMessageBody();
                    Log.i(TAG, "Message recieved: " + messages[0].getMessageBody());
                    String patternString = "בסך (\\d{0,10})";
                    // String patternString = "$?ב*.";
                    Pattern pattern = Pattern.compile(patternString);
                    Matcher matcher = pattern.matcher(text);
                    if(text.contains("מאסטרקארד") || text.contains("MasterCard") || text.contains("Mastercard")|| text.contains("www.cal-online.co.il")){

                        if(text.contains("USD")){

                            if (matcher.find())
                            {
                                Date c = Calendar.getInstance().getTime();
                                DateFormat sdf1 = new SimpleDateFormat("YYYY-MM-dd");
                                myDbHandler.insertuserdata("La Case De Papel", Double.parseDouble(matcher.group(1))*3.31,"Mastercard Ends With 0912",sdf1.format(c));
                            }
                        }
                        else {

                            if (matcher.find())
                            {
                                Date c = Calendar.getInstance().getTime();
                                DateFormat sdf1 = new SimpleDateFormat("YYYY-MM-dd");
                                myDbHandler.insertuserdata("La Case De Papel", Double.parseDouble(matcher.group(1)),"Mastercard Ends With 0912",sdf1.format(c));
                            }
                        }

                    }
                    else if( text.contains("Isracard") || text.contains("ישראכרט")|| text.contains("isracard")){

                        if(text.contains("USD")){
                            if (matcher.find())
                            {
                                Date c = Calendar.getInstance().getTime();
                                DateFormat sdf1 = new SimpleDateFormat("YYYY-MM-dd");
                                myDbHandler.insertuserdata("La Case De Papel", Double.parseDouble(matcher.group(1))*3.31,"Isracard Ends With 1223",sdf1.format(c));
                            }
                        }
                        else {
                            if (matcher.find())
                            {
                                Date c = Calendar.getInstance().getTime();
                                DateFormat sdf1 = new SimpleDateFormat("YYYY-MM-dd");
                                myDbHandler.insertuserdata("La Case De Papel", Double.parseDouble(matcher.group(1)),"Isracard Ends With 1223",sdf1.format(c));
                            }
                        }


                    }
                }
            }
        }
    }
}