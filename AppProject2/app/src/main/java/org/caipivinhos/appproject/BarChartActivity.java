package org.caipivinhos.appproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
//

public class BarChartActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String date;
    Button dateBt;
    private static final String TAG = "BarChartActivity";

    // variable for our bar chart
    BarChart barChart;

    // variable for our bar data set.
    BarDataSet barDataSet1;

    // array list for storing entries.
    ArrayList barEntries;

    int[] stress_levels = null;
    int numSessions;
    int startTime;

    DatabaseManager db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        ActionBar bar = getSupportActionBar();

        if (bar != null){
            bar.setIcon(R.drawable.icon);
            bar.setTitle("BeCalm");
        }
        db = new DatabaseManager(this);

        Date time = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(time);
        date  = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);

        dateBt = findViewById(R.id.buttonDate);

        dateBt.setText(date);
        setBarChartData();

        //method for Date selection
        dateBt.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });

    }

    private void setBarChartData(){
        // stress level (percentage) acquisition from database
        ArrayList<Integer> stress_levels_arraylist = db.getSessionsPercentageByDate(date);

        if(stress_levels_arraylist.size() == 0){
            Toast.makeText(this, "There's no data available for that day yet :)", Toast.LENGTH_LONG).show();
            return;
        }

        stress_levels = new int[stress_levels_arraylist.size()];
        for (int i = 0; i < stress_levels_arraylist.size(); i++){
            stress_levels[i] = stress_levels_arraylist.get(i);
        }

        // number of sessions in the specified day
        numSessions = stress_levels.length;
        // start time of acquisition in the specified day (start time of the first session)
        startTime = db.getHourBeginReport(date);

        // initializing variable for bar chart.
        barChart = findViewById(R.id.idBarChart);

        // attributing colors to bars according to the stress level
        int [] colorLabels = colorLabels(stress_levels);

        // creating a string array for displaying intervals. Intervals have a fixed duration of 2h
        String[] intervals = timeIntervals(numSessions, startTime);

        // creating a new bar data set.
        barDataSet1 = new BarDataSet(getBarEntries(numSessions, stress_levels), "Stress Levels");
        barDataSet1.setValueTextSize(14f);
        barDataSet1.setColors(colorLabels);

        // below line hides the legend (stress level)
        barChart.getLegend().setEnabled(false);

        // below line is to add bar data set to our bar data.
        BarData data = new BarData(barDataSet1);

        // after adding data to our bar data we
        // are setting that data to our bar chart.
        barChart.setData(data);

        // below line is to remove description
        // label of our bar chart.
        barChart.getDescription().setEnabled(false);

        barChart.setDrawGridBackground(false);

        // below line is to get x axis
        // of our bar chart.
        XAxis xAxis = barChart.getXAxis();

        // below line is to set value formatter to our x-axis and
        // we are adding our intervals to our x axis.
        xAxis.setValueFormatter(new IndexAxisValueFormatter(intervals));

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
        yAxisLeft.setAxisMinimum(100);
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
    }

    // array list for first set
    private ArrayList<BarEntry> getBarEntries(int numSessions, int[] stress_levels) {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        for (int i = 0; i < numSessions; i++) {
            float f = i+1;
            barEntries.add(new BarEntry(f, stress_levels[i])); //Começa em 1f
        }

        return barEntries;
    }

    // Retorna intervalos de tempo de medição (duração de 5min traduzidos para 2h)
    // Se a hora de início de medição foi 9h e o dia teve duas sessões, a função retorna 9:00 - 11:00 e 11:00 - 13:00
    private String[] timeIntervals(int numSessions, int startTime){
        //ArrayList<String> timeIntervals = new ArrayList<String>();
        String[] timeIntervals = new String[numSessions+1];
        int intervalBegin = startTime - 2;
        int intervalEnd = intervalBegin + 2;

        timeIntervals[0]="";
        for (int i = 1; i <= numSessions; i++) {
            intervalBegin = intervalBegin + 2;
            intervalEnd = intervalEnd + 2;

            if(intervalBegin >= 24){
                intervalBegin = intervalBegin - 24;
            }
            if(intervalEnd >= 24){
                intervalEnd = intervalEnd - 24;
            }

            timeIntervals[i] = String.join("",String.valueOf(new Integer(intervalBegin)),":00 - ",String.valueOf(new Integer(intervalEnd)),":00");
        }

        return timeIntervals;
    }

    private int[] colorLabels(int[] stress_levels){
        int[] color_labels = new int[stress_levels.length];

        for (int i = 0; i < stress_levels.length; i++)
            color_labels[i] = getColorLabel(stress_levels[i]);

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
            String message = "You're already at Bar Chart data";
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

    //method for Date picking
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String newDate = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        if(db.getSessionsPercentageByDate(newDate).size() == 0) {
            Toast.makeText(this, "There's no data available for that day yet :)", Toast.LENGTH_LONG).show();
        } else {
            date = newDate;
            Log.d(TAG, "onDateSet: date " + date);
            dateBt.setText(date);
            setBarChartData();
        }

    }
}