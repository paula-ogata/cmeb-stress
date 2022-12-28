package org.caipivinhos.appproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class NamePrompt extends AppCompatActivity {
    EditText nameET;
    EditText ageET;
    RadioGroup rg;
    SharedPreferences sharedpreferences;
    String prevStarted = "prevStarted";
    String gender = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_prompt);

        ActionBar bar = getSupportActionBar();

        if (bar != null){
            bar.setIcon(R.drawable.icon);
            bar.setTitle("BeCalm");
        }
        nameET = findViewById(R.id.txtUsername);
        ageET = findViewById(R.id.txtAge);

        findViewById(R.id.button).setOnClickListener(this::btSendOnClick);

    }

    public void btSendOnClick(View view){

        String username = nameET.getText().toString();
        String ageString = ageET.getText().toString();
        switch(((RadioGroup)findViewById(R.id.rg_types)).getCheckedRadioButtonId()) {
            case R.id.rb_male:
                gender = "male";
                break;
            case R.id.rb_female:
                gender = "female";
                break;
        }

        if(Objects.equals(gender, "null") || username.isEmpty() || ageString.isEmpty()) {
            Toast.makeText(this, "Please complete your information first", Toast.LENGTH_LONG).show();
        } else {
            Integer age = Integer.valueOf(ageString);
            DatabaseManager db = new DatabaseManager(this);
            db.AddUser(username,gender,age);

            sharedpreferences = getSharedPreferences("App", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(prevStarted, Boolean.TRUE);
            editor.apply();

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }
}