package org.caipivinhos.appproject;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_acquisition);
        context = this;

        stressValue = findViewById(R.id.stressValue);

        try {
            //vj.connectToVJ(this);
            Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            Log.d(TAG, "InstantAcquisition: Unable to Connect to VitalJacket");
        }

        bt = findViewById(R.id.button);
        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        spinner.isIndeterminate();

        bt.setOnClickListener(v -> {
            spinner.setVisibility(View.VISIBLE);
            InstantTest runnable = new InstantTest();
            new Thread(runnable).start();
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
            String message = "Already in home - FOLEIRO MUDAR";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        return (super.onOptionsItemSelected(item));
    }

    private class InstantTest implements Runnable {

        @Override
        public void run() {
            //VitalJacketManager vjRun = new VitalJacketManager();
            double valueR = 0;
            try {
                //VitalJacketManager.connectToVJ(context);
                Log.d(TAG, "run: Connected to VJ");
                valueR = VitalJacketManager.instantSession(context);
            } catch (Exception e) {
                Log.d(TAG, "Runnable: Caught Error " + e.getMessage());
            }
            double finalValueR = valueR;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    stressValue.setText(String.valueOf(Math.round(finalValueR)));
                    spinner.setVisibility(View.GONE);
                }
            });
        }
    }
}