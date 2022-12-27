package org.caipivinhos.appproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class InstantAcquisition extends AppCompatActivity {
    Button bt;
    ProgressBar spinner;
    TextView stressValue;
    Handler mainHandler = new Handler();
    private static final String TAG = "InstantAcquisition";
    Context context;
    DatabaseManager db = null;
    double mediumLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_acquisition);

        ActionBar bar = getSupportActionBar();
        if (bar != null){
            bar.setIcon(R.drawable.icon);
            bar.setTitle("BeCalm");
        }

        db = new DatabaseManager(this);
        mediumLevel = db.getMediumLevel();
        context = this;

        stressValue = findViewById(R.id.stressValue);

        bt = findViewById(R.id.button);
        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        spinner.isIndeterminate();

        bt.setOnClickListener(v -> {
            if(!VitalJacketManager.checkIfConnected()) {
                Toast.makeText(this, "Please Connect To VitalJacket First", Toast.LENGTH_LONG).show();
            } else {
                spinner.setVisibility(View.VISIBLE);
                InstantTest runnable = new InstantTest();
                new Thread(runnable).start();
            }
        });
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
            startActivity(new Intent(this, BarChartActivityWeek.class));
            return(true);
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
            String message = "You're already in the Instant Measurement page";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        return (super.onOptionsItemSelected(item));
    }

    private class InstantTest implements Runnable {

        @Override
        public void run() {
            double valueR = 0;
            try {
                Log.d(TAG, "run: Connected to VJ");
                valueR = VitalJacketManager.instantSession(context, mediumLevel);
            } catch (Exception e) {
                Log.d(TAG, "Runnable: Caught Error " + e.getMessage());
            }
            double finalValueR = valueR;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(finalValueR == -1) {
                        Toast.makeText(context, "Please Choose de device first", Toast.LENGTH_LONG).show();
                    } else {
                        stressValue.setText(String.valueOf(Math.round(finalValueR)));
                        spinner.setVisibility(View.GONE);
                        if (finalValueR <= 40) {
                            stressValue.setBackground(getDrawable(R.drawable.show_stress_layout));
                        } else if(finalValueR > 40 && finalValueR <= 70){
                            stressValue.setBackground(getDrawable(R.drawable.show_stress_layout_2));
                        } else {
                            stressValue.setBackground(getDrawable(R.drawable.show_stress_layout_3));
                        }
                    }
                }
            });
        }
    }
}