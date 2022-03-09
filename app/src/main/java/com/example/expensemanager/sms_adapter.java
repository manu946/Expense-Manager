package com.example.expensemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.button.MaterialButton;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sms_adapter extends RecyclerView.Adapter<sms_adapter.ViewHolder> {
    Context context;
    List<Expense_Trans_Model> strings;

    public sms_adapter(Context context, List<Expense_Trans_Model> strings) {
        this.context = context;
        this.strings = strings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.trans_layout,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        SharedPreferences sharedPreferences=context.getSharedPreferences("MAIN",Context.MODE_PRIVATE);
        boolean lng=sharedPreferences.getBoolean("IS_USD",false);
        if(lng){

            double in_usd=0.30*strings.get(position).getAmount();
            holder.amount.setText(Double.toString(in_usd)+"$");
        }
        else {
            holder.amount.setText(Double.toString(strings.get(position).getAmount())+"₪");
        }

        holder.card.setText(strings.get(position).getCard());
        holder.date.setText(strings.get(position).getDate());
        holder.where.setText(strings.get(position).getPlace());

    }

    @Override
    public int getItemViewType(int position) {
       return position;
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView where,date,card,amount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Position 0 findview
            where=itemView.findViewById(R.id.where);
            date=itemView.findViewById(R.id.date);
            card=itemView.findViewById(R.id.card);
            amount=itemView.findViewById(R.id.amount);


        }
    }
}
