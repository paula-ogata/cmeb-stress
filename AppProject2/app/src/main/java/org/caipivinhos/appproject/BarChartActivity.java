package org.caipivinhos.appproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class BarChartActivity extends AppCompatActivity {
    // variable for our bar chart
    BarChart barChart;

    // variable for our bar data set.
    BarDataSet barDataSet1;

    // array list for storing entries.
    ArrayList barEntries;

    int[] stress_levels = null;
    int numSessions;
    int startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        DatabaseManager db = new DatabaseManager(this);

        ArrayList<Integer> stress_levels_arraylist = db.getSessionsPercentageByDate("13/12/2022"); // Enviado da database: stress level de cada session do dia
        stress_levels = new int[stress_levels_arraylist.size()];
        for (int i = 0; i < stress_levels_arraylist.size(); i++){
            stress_levels[i] = stress_levels_arraylist.get(i);
        }

        numSessions = stress_levels.length; // Número de sessions no dia
        startTime = db.getHourBeginReport("13/12/2022"); // Start time da primeira sessão do dia (start time do dia

        // initializing variable for bar chart.
        barChart = findViewById(R.id.idBarChart);

        int [] colorLabels = colorLabels(stress_levels);

        // creating a string array for displaying intervals.
        //String[] intervals = new String[]{"","8:30 - 9:00","9:00 - 9:30","9:30 - 10:00", "10:00 - 10:30", "10:30 - 11:00", "11:00 - 11:30", "11:30 - 12:00", "12:00 - 12:30","12:30 - 13:00","13:00 - 13:30","13:30 - 14:00", "14:00 - 14:30", "14:30 - 15:00", "15:00 - 15:30", "15:30 - 16:00", "16:00 - 16:30"};
        String[] intervals = timeIntervals(numSessions, startTime);

        // creating a new bar data set.
        barDataSet1 = new BarDataSet(getBarEntries(numSessions, stress_levels), "Stress Levels");
        barDataSet1.setColor(getApplicationContext().getResources().getColor(R.color.purple_200)); // APLICAR TONS DE ACORDO COM VALOR
        barDataSet1.setValueTextSize(12f);
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

        // below line is to get x axis
        // of our bar chart.
        XAxis xAxis = barChart.getXAxis();

        // below line is to set value formatter to our x-axis and
        // we are adding our intervals to our x axis.
        xAxis.setValueFormatter(new IndexAxisValueFormatter(intervals));

        //TESTING
        barChart.setDrawGridBackground(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(intervals));

        YAxis yAxis = barChart.getAxisRight();
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawAxisLine(true);
        yAxis.setEnabled(false);

        // below line is to set center axis
        // labels to our bar chart.
        xAxis.setCenterAxisLabels(false);

        // below line is to set position
        // to our x-axis to bottom.
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

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
        data.setBarWidth(0.8f);

        // below line is to set minimum
        // axis to our chart.
        barChart.getXAxis().setAxisMinimum(0.3f);

        // below line is to
        // animate our chart.
        barChart.animateY(2000);

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

        timeIntervals[0]="";
        for (int i = 1; i <= numSessions; i++) {
            timeIntervals[i] = String.join("",String.valueOf(new Integer(startTime+2*(i-1))),":00 - ",String.valueOf(new Integer(startTime+2*i)),":00");
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
            String message = "Already in home - FOLEIRO MUDAR";
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