package org.caipivinhos.appproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    boolean isRunning = false;
    TextView stressValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_acquisition);

        stressValue = findViewById(R.id.stressValue);
        bt = (Button)findViewById(R.id.button);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        spinner.isIndeterminate();

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                double value = (new VitalJacketManager()).instantSession();
                stressValue.setText(String.valueOf(value));
                spinner.setVisibility(View.GONE);

                /*
                if(isRunning) {
                    spinner.setVisibility(View.GONE);
                    isRunning = false;
                }
                else {
                    spinner.setVisibility(View.VISIBLE);
                    isRunning = true;
                }
                */
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
}