package org.caipivinhos.appproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import android.app.Application;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

// From PieChart
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
//

// For clicking on the BarChart
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.listener.OnDrawListener;
//

import java.time.*;
import java.util.Objects;

public class BarChartActivityWeek extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String date;
    String dateText;
    String selectedDate;
    String dayString;
    String monthString;
    boolean updateChart = true;
    Button dateBt;
    private static final String TAG = "BarChartActivityWeek";
    String GET_DATE = "Date_Intent_Info";

    // variable for the bar chart
    BarChart barChart;

    // variable for the bar data set.
    BarDataSet barDataSet;

    // array list for storing entries.
    ArrayList barEntries;

    int[] avg_stress_levels = null;
    String startDate;
    String[] weekDays;

    DatabaseManager db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart_week);

        ActionBar bar = getSupportActionBar();

        if (bar != null){
            bar.setIcon(R.drawable.icon);
            bar.setTitle("BeCalm");
        }
        db = new DatabaseManager(this);

        Date time = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(time);

        calendar.setMinimalDaysInFirstWeek(1);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        dayString = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        monthString = String.valueOf(calendar.get(Calendar.MONTH) + 1);

        date  = dayString + "/" + monthString + "/" + calendar.get(Calendar.YEAR);
        // Adding "0" in front of single digits
        if (dayString.length()==1){
            dayString = "0"+dayString;
        }
        if (monthString.length()==1){
            monthString = "0"+monthString;
        }
        dateText  = dayString + "/" + monthString + "/" + calendar.get(Calendar.YEAR);

        weekDays = getWeekDays(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dateBt = findViewById(R.id.buttonDate);

        dateBt.setText("Week Start: "+dateText);
        setBarChartData(updateChart);

        //method for Date selection
        dateBt.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });

    }

    private void setBarChartData(boolean updateChart){

        if (!updateChart){return;}

        // The start date is always a monday (same date as shown in the button above the graph)
        startDate = date;

        int[] avg_stress_levels = getAvgStressLevelsWeek(weekDays);

        // Checks if no measurement has been acquired for any of that week's days
        if(Arrays.equals(avg_stress_levels,new int[7])){
            Toast.makeText(this, "There's no data available for that week yet :)", Toast.LENGTH_LONG).show();
            return;
        }

        // initializing variable for bar chart.
        barChart = findViewById(R.id.idBarChart);

        // attributing colors to bars according to the stress level
        int [] colorLabels = colorLabels(avg_stress_levels);

        // creating a string array for displaying the days of the chosen week (7 days)
        String[] dayLabels = getDayLabels(weekDays);

        // creating a new bar data set.
        barDataSet = new BarDataSet(getBarEntries(avg_stress_levels), "Stress Levels");
        barDataSet.setValueTextSize(14f);
        barDataSet.setColors(colorLabels);

        // below line hides the legend (stress level)
        barChart.getLegend().setEnabled(false);

        // below line is to add bar data set to our bar data.
        BarData data = new BarData(barDataSet);

        // after adding data to our bar data we
        // are setting that data to our bar chart.
        barChart.setData(data);

        // The following lines enable/disable chart features of interest and sets its layout

        // below line is to remove description
        // label of our bar chart.
        barChart.getDescription().setEnabled(false);

        barChart.setDrawGridBackground(false);

        // below line is to get x axis
        // of our bar chart.
        XAxis xAxis = barChart.getXAxis();

        // below line is to set value formatter to our x-axis and
        // we are adding our intervals to our x axis.
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dayLabels));

        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);

        // below line is to set center axis
        // labels to our bar chart.
        xAxis.setCenterAxisLabels(false);

        // below line is to set position
        // to our x-axis to bottom.
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxisLeft.setDrawGridLines(true);
        yAxisLeft.setDrawAxisLine(true);
        yAxisLeft.setAxisMaximum(100);
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setLabelCount(6);

        // below line is to make our
        // bar chart as draggable
        // (only in the x axis)
        barChart.setDragXEnabled(true);
        barChart.setDragYEnabled(false);

        // below line is to NOT allow
        // two-tap or pintch zoom.
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setScaleXEnabled(false);
        barChart.setScaleYEnabled(false);

        // below line is to make visible
        // range for our bar chart.
        barChart.setVisibleXRangeMaximum(5);

        // we are setting width of
        // bar in below line.
        data.setBarWidth(0.6f);

        // below line is to set minimum
        // axis to our chart.
        barChart.getXAxis().setAxisMinimum(0.5f);

        // below line is to
        // animate our chart.
        barChart.animateY(1500);

        barChart.setFitBars(false);

        // below line is to invalidate
        // our bar chart.
        barChart.invalidate();

        barChart.setHighlightPerTapEnabled(true);

        // Methods for interacting wit the chart - Redirects the user to the corresponding Daily Report upon clicking on a stress bar
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
           @Override
           public void onValueSelected(Entry e, Highlight h) {
               e.getData();
               Log.d("VAL SELECTED","Value: " + e.getY() + ", xIndex: " + e.getX() + ", DataSet index: " + h.getDataSetIndex());
               if (e.getY() != 0.0){
                   int index = (int) e.getX();
                   selectedDate = weekDays[index];
                   Log.d("Selected Date",selectedDate);

                   Intent i = new Intent(getApplicationContext(), BarChartActivity.class);
                   i.putExtra(GET_DATE, selectedDate);
                   startActivity(i);
               }

           }

           @Override
           public void onNothingSelected() {
               Log.d("BAR_CHART_SAMPLE", "nothing selected; X is ");

           }

       });

    }

    // Array list for first set
    private ArrayList<BarEntry> getBarEntries(int[] avg_stress_levels) {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        for (int i = 0; i < 7; i++) {
            float f = i+1;
            barEntries.add(new BarEntry(f, avg_stress_levels[i])); //Começa em 1f
        }

        return barEntries;
    }

    // Retrieves the average stress level for each day of the week of the chosen day
    private int[] getAvgStressLevelsWeek (String[] weekDays){
        int[] AvgStressLevelsWeek = new int[7];

        for (int i = 0; i < 7; i++) {
            if (db.getAvgPercentageByDate(weekDays[i+1]) == null){
                AvgStressLevelsWeek[i] = 0;
            } else {
                AvgStressLevelsWeek[i] = db.getAvgPercentageByDate(weekDays[i + 1]);
            }
        }

        return AvgStressLevelsWeek;
    }

    // Finds the dates of all of the days in the week of the chosen day
    private String[] getWeekDays(int year, int month, int dayOfMonth){
        String[] weekDays = new String[8];

        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        c.setMinimalDaysInFirstWeek(1);
        c.setFirstDayOfWeek(Calendar.MONDAY);

        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String startDate = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);

        weekDays[0] = "";
        weekDays[1] = startDate;

        for (int i = 2; i <= 7; i++) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            weekDays[i] = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        }

        return weekDays;
    }

    private String[] getDayLabels(String[] weekDays){
        String[] weekDayNames = new String[]{"MON","TUE","WED","THU","FRI","SAT","SUN"};
        String[] weekDayLabels = new String[8];
        String day;
        String month;

        weekDayLabels[0] = weekDays[0];
        for (int i = 1; i <= 7; i++) {
            String[] aux_array = weekDays[i].split("/",0);
            day = aux_array[0];
            month = aux_array[1];

            // Adding "0" in front of single digits
            if (day.length()==1){
                day = "0"+day;
            }
            if (month.length()==1){
                month = "0"+month;
            }

            weekDayLabels[i] = weekDayNames[i-1] + " " + String.join("/",day,month);
        }

        return weekDayLabels;
    }

    private int[] colorLabels(int[] avg_stress_levels){
        int[] color_labels = new int[avg_stress_levels.length];

        for (int i = 0; i < avg_stress_levels.length; i++)
            color_labels[i] = getColorLabel(avg_stress_levels[i]);

        return color_labels;
    }

    private int getColorLabel(int stress_level) {
        // 100% - 70% (severe) 70% - 40% (high) 40% - 0% (moderate)
        if (stress_level > 70){
            return Color.parseColor("#EF5350");
        } else if (stress_level <=70 & stress_level > 40) {
            return Color.parseColor("#FFA726");
        } else{
            return Color.parseColor("#29B6F6");
        }
    }

    //method for Date picking
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        c.setMinimalDaysInFirstWeek(1);
        c.setFirstDayOfWeek(Calendar.MONDAY);

        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        dayString = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        monthString = String.valueOf(c.get(Calendar.MONTH) + 1);

        String newDate = dayString + "/" + monthString + "/" + c.get(Calendar.YEAR);
        // Adding "0" in front of single digits
        if (dayString.length()==1){
            dayString = "0"+dayString;
        }
        if (monthString.length()==1){
            monthString = "0"+monthString;
        }
        String newDateText = dayString + "/" + monthString + "/" + c.get(Calendar.YEAR);

        weekDays = getWeekDays(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        avg_stress_levels = getAvgStressLevelsWeek(weekDays);

        if(Arrays.equals(avg_stress_levels,new int[7])) {
            Toast.makeText(this, "There's no data available for that week yet :)", Toast.LENGTH_LONG).show();
        } else  {
            if (!Objects.equals(date, newDate)){
                updateChart = true;
                date = newDate;
                dateText = newDateText;
            } else {
                updateChart = false;
                Log.d(TAG, "onDateSet: date " + date);
            }
            dateBt.setText("Week Start: "+dateText);
            setBarChartData(updateChart);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            startActivity(new Intent(this, PieChartActivity.class));
            return(true);
        }
        else if (item.getItemId()==R.id.chart) {
            startActivity(new Intent(this, BarChartActivity.class));
            return(true);
        }
        else if (item.getItemId()==R.id.chartWeek) {
            String message = "You're already at the Weekly Report page";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        else if(item.getItemId()==R.id.chooseBt) {
            startActivity(new Intent(this, ChooseBTDevice.class));
            return(true);
        }
        else if (item.getItemId()==R.id.about){
            startActivity(new Intent(this, AboutActivity.class));
            return(true);
        }
        else if (item.getItemId()==R.id.hiw) {
            startActivity(new Intent(this, HowWorksActivity.class));
            return(true);
        } else if (item.getItemId() == R.id.instant) {
            startActivity(new Intent(this, InstantAcquisition.class));
            return(true);
        }
        return (super.onOptionsItemSelected(item));
    }

}