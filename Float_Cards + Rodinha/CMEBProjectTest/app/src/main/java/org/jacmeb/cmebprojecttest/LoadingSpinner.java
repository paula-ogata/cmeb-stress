package org.jacmeb.cmebprojecttest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class LoadingSpinner extends AppCompatActivity {
    Button bt;
    ProgressBar spinner;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_spinner);

        bt = (Button)findViewById(R.id.button);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        spinner.isIndeterminate();

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRunning) {
                    spinner.setVisibility(View.GONE);
                    isRunning = false;
                }
                else {
                    spinner.setVisibility(View.VISIBLE);
                    isRunning = true;
                }
            }
        });
    }
}