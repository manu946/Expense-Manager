package com.example.expensemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.Data.MyDbHandler;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class chart_fragment extends Fragment {

    public chart_fragment() {
        // Required empty public constructor
    }


    BarChart pieChart, pie2;
    TextView total_spent_today;
    ArrayList<BarEntry> barEntries = new ArrayList<>();
    ArrayList<BarEntry> barEntries2 = new ArrayList<>();
    List<String> months = new ArrayList<>();
    double whole_amount = 0;
    boolean isUsd;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_chart_fragment, null);
        months.add("January");
        months.add("January");
        months.add("Febuary");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");
        pieChart = viewGroup.findViewById(R.id.pie_chart);
        pie2 = viewGroup.findViewById(R.id.pie_chart2);
        total_spent_today = viewGroup.findViewById(R.id.total_spent_today);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MAIN", Context.MODE_PRIVATE);
        isUsd = sharedPreferences.getBoolean("IS_USD", false);
        MyDbHandler myDbHandler = new MyDbHandler(getContext());
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        String monthStart = dateFormat.format(c.getTime());
        c = Calendar.getInstance(); // reset
        String today = dateFormat.format(c.getTime());
        List<Expense_Trans_Model> expense_trans_models = myDbHandler.get_bw_dates(monthStart, today);
        if (expense_trans_models.size() > 0) {
            if (isUsd) {


                for (Expense_Trans_Model ff : expense_trans_models) {
                    whole_amount = whole_amount + ff.getAmount();
                    String formattedDate = ff.getDate();
                    String day = formattedDate.substring(8, 10);
                    Log.d("DAYS IS", day);
                    int amount = (int) (ff.getAmount() * 0.30);
                    barEntries.add(new BarEntry(Integer.parseInt(day), amount));
                }

                BarDataSet barDataSet = new BarDataSet(barEntries, "Expense");
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(0f);

                BarData barData = new BarData(barDataSet);

                pieChart.setFitBars(true);
                pieChart.setData(barData);
                pieChart.getDescription().setText("Expense Of Today Month");
                pieChart.animateY(2000);

                double in_usd = 0.30 * whole_amount;
                total_spent_today.setText(Double.toString(in_usd) + "$");
            } else {

                for (Expense_Trans_Model ff : expense_trans_models) {
                    whole_amount = whole_amount + ff.getAmount();
                    String formattedDate = ff.getDate();
                    String day = formattedDate.substring(8, 10);
                    Log.d("DAYS IS", day);
                    int amount = (int) ff.getAmount();
                    barEntries.add(new BarEntry(Integer.parseInt(day), amount));

                }

                BarDataSet barDataSet = new BarDataSet(barEntries, "Expense");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(0f);

                BarData barData = new BarData(barDataSet);

                pieChart.setFitBars(true);
                pieChart.setData(barData);
                pieChart.getDescription().setText("Expense Of Today Month");
                pieChart.animateY(2000);

                double in_usd = whole_amount;
                total_spent_today.setText(Double.toString(in_usd) + "₪");
            }
        }

        for (int h = 1; h <= 12; h++) {
            if (h < 10) {
                String formating = "0" + h;
                barEntries2.add(new BarEntry(h, (int) myDbHandler.particular_month(formating, "2022")));
            } else {
                barEntries2.add(new BarEntry(h, (int) myDbHandler.particular_month(String.valueOf(h), "2022")));
            }

        }
        BarDataSet barDataSet = new BarDataSet(barEntries2, "NOTHER");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(0f);

        BarData barData = new BarData(barDataSet);

        XAxis xAxis = pie2.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(months.size());
        xAxis.setLabelRotationAngle(270);


        pie2.setFitBars(true);
        pie2.setData(barData);
        pie2.getDescription().setText("");
        pie2.animateY(3000);
        pie2.invalidate();


        // Inflate the layout for this fragment
        return viewGroup;
    }
}