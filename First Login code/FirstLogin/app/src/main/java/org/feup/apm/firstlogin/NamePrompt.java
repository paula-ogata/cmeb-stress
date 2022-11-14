package org.feup.apm.firstlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.os.Bundle;
import android.widget.EditText;

public class NamePrompt extends AppCompatActivity {
    String prevStarted = "prevStarted";

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (!sharedpreferences.getBoolean(prevStarted, false)) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(prevStarted, Boolean.TRUE);
            editor.apply();
        } else {
            moveToSecondary();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_prompt);
        findViewById(R.id.button).setOnClickListener(this::btSendOnClick);
    }

    public void btSendOnClick(View view){
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        EditText editText = findViewById(R.id.txtUsername);
        String message = editText.getText().toString();
        prefs.edit().putString("username", message).commit();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void moveToSecondary(){
        // use an intent to travel from one activity to another.
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}