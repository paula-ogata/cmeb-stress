package org.caipivinhos.appproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    double value;
    VitalJacketManager vj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_acquisition);

        vj = new VitalJacketManager();
        try {
            vj.connectToVJ(this);
            Toast.makeText(this, "Ligado", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erro", Toast.LENGTH_LONG).show();
        }
        stressValue = findViewById(R.id.stressValue);
        bt = (Button)findViewById(R.id.button);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        spinner.isIndeterminate();

        bt.setOnClickListener(v -> {
            spinner.setVisibility(View.VISIBLE);

            //Thread thread = new Thread(vj);
            //thread.start();
            AsyncTask<Void, Void, Double> test = new InstantTest().execute();

            new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                    // TO DO If necessary
                }

                public void onFinish() {
                    try {
                        value = (double) test.get();
                        stressValue.setText(String.valueOf(value));
                        spinner.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
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

    private class InstantTest extends AsyncTask<Void, Void, Double> {
        @Override
        protected Double doInBackground(Void... voids) {
            Thread thread = new Thread(vj);
            thread.start();
            return vj.getInstantValue();
        }
    }
}