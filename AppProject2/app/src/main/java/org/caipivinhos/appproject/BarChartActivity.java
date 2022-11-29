package org.caipivinhos.appproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class BarChartActivity extends AppCompatActivity {
    ArrayList barArraylist;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        BarChart barChart = findViewById(R.id.barchart);
        // Adds data to the barArraylist
        getData();
        BarDataSet barDataSet = new BarDataSet(barArraylist,"Stress Levels");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        //color bar data set
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        //text color
        barDataSet.setValueTextColor(Color.BLACK);
        //settting text size
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);
    }

    private void getData()
    {
        barArraylist = new ArrayList();
        barArraylist.add(new BarEntry(2f,10));
        barArraylist.add(new BarEntry(3f,10));
        barArraylist.add(new BarEntry(4f,30));
        barArraylist.add(new BarEntry(5f,25));
        barArraylist.add(new BarEntry(6f,50));

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
        else if (item.getItemId()==R.id.about){
            startActivity(new Intent(this, AboutActivity.class));
            return(true);
        }
        else if (item.getItemId()==R.id.hiw) {
            startActivity(new Intent(this, HowWorksActivity.class));
            return(true);
        }
        return (super.onOptionsItemSelected(item));
    }
}