package com.example.expensemanager;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.Data.MyDbHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class home_fragment extends Fragment {



    public home_fragment() {
        // Required empty public constructor
    }
    List<Expense_Trans_Model> expense_trans_models=new ArrayList<>();
    TextView not_found,total_Spent;
    RecyclerView recyclerView;
    Cursor c;
    SwipeRefreshLayout swipeRefreshLayout;
    MyDbHandler myDbHandler;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home_fragment, null);
    myDbHandler=new MyDbHandler(getContext());

    progressBar=viewGroup.findViewById(R.id.progress_circular);
        recyclerView=viewGroup.findViewById(R.id.recyler);
        not_found=viewGroup.findViewById(R.id.exp);
        swipeRefreshLayout=viewGroup.findViewById(R.id.swipe);
        total_Spent=viewGroup.findViewById(R.id.total_spent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myDbHandler.delete_all();
                getAllSms();
                if(myDbHandler.getallExpense().size()>0){
                    recyclerView.setVisibility(View.VISIBLE);
                    not_found.setVisibility(View.GONE);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                    recyclerView.setAdapter(new sms_adapter(getContext(),myDbHandler.getallExpense()));
                }
                else {
                    not_found.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }


                swipeRefreshLayout.setRefreshing(false);
            }
        });
        if(myDbHandler.getallExpense().size()>0){
            recyclerView.setVisibility(View.VISIBLE);
            not_found.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
            recyclerView.setAdapter(new sms_adapter(getContext(),myDbHandler.getallExpense()));
        }
        else {
            not_found.setVisibility(View.VISIBLE);
           recyclerView.setVisibility(View.INVISIBLE);
        }

        SharedPreferences sharedPreferences=getContext().getSharedPreferences("MAIN", Context.MODE_PRIVATE);
        boolean lng=sharedPreferences.getBoolean("IS_USD",false);
        if(lng){

            double in_usd=0.30*myDbHandler.all_amount();
            total_Spent.setText("Total Spent : "+Double.toString(in_usd)+"$");
        }
        else {
            double in_usd=myDbHandler.all_amount();
            total_Spent.setText("Total Spent : "+Double.toString(in_usd)+"₪");

        }


        return viewGroup;
    }
    @SuppressLint("Range")
    public List<Sms> getAllSms() {
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms = new Sms();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getContext().getContentResolver();

        c = cr.query(message, null, null, null, null);
        getActivity().startManagingCursor(c);
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
            Toast.makeText(getContext(), "You Have No SMS", Toast.LENGTH_SHORT).show();

        }

        return lstSms;
    }
}