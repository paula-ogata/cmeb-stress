package org.jacmeb.ecgapp_second;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    public final static String ID_EXTRA = "org.jacmeb.ecgApp_Second.FILE";
    AssetManager asMan;
    String[] fNames;
    Button bt1, bt2, startBt;
    boolean worked = false;
    int file_num = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        asMan = getAssets();
        try {
            fNames = asMan.list("ecgFiles");
            worked = true;
        } catch (Exception ex) {
            fNames = new String[1];
            fNames[0] = "No files found";
        }

        bt1 = (Button) findViewById(R.id.ecg1_bt);
        bt2 = (Button) findViewById(R.id.ecg2_bt);

        if(worked) {
            bt1.setText(fNames[0]);
            bt2.setText(fNames[1]);
        }
        else {
            //to do...
        }

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file_num = 0;
                bt1.setBackgroundColor(Color.parseColor("#CD5B45"));
                bt2.setBackgroundColor(Color.parseColor("#1976D2"));
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file_num = 1;
                bt2.setBackgroundColor(Color.parseColor("#CD5B45"));
                bt1.setBackgroundColor(Color.parseColor("#1976D2"));
            }
        });

        startBt = (Button) findViewById(R.id.start_bt);
        startBt.setOnClickListener((View v)->onBtStartClick());
    }


    void onBtStartClick() {
        if (file_num != -1) {
            Intent i = new Intent(this, PlotActivity.class);
            String fileName = fNames[file_num];
            i.putExtra(ID_EXTRA, fileName);
            startActivity(i);
        }
        else {
            Toast.makeText(this, "No item Selected", Toast.LENGTH_LONG).show();
        }
    }
}