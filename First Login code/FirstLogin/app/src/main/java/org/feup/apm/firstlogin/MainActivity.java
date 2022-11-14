package org.feup.apm.firstlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        username = prefs.getString("username", "UNKNOWN");

        TextView tvMessage = new TextView(this);
        tvMessage.setTextSize(30f);
        tvMessage.setText("Bem-vinda "+username);

        // Set the text view as the activity layout
        setContentView(tvMessage);
    }
}