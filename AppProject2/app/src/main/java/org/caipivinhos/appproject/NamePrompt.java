package org.caipivinhos.appproject;

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
import android.widget.RadioGroup;
import android.widget.TextView;

public class NamePrompt extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_prompt);
        findViewById(R.id.button).setOnClickListener(this::btSendOnClick);
    }

    public void btSendOnClick(View view){
        EditText nameET = findViewById(R.id.txtUsername);
        EditText ageET = findViewById(R.id.txtAge);
        RadioGroup rg = findViewById(R.id.rg_types);
        String username = nameET.getText().toString();
        Integer age = Integer.valueOf(ageET.getText().toString());
        int genderId = rg.getCheckedRadioButtonId();
        String gender;
        if(genderId == 0) {
            gender = "male";
        }
        else {
            gender = "female";
        }

        DatabaseManager db = new DatabaseManager(this);
        db.AddUser(username,gender,age);

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}