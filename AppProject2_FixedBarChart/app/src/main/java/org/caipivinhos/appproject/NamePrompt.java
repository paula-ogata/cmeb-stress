package org.caipivinhos.appproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

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
        if(genderId == 0)
            gender = "Male";
        else
            gender = "Female";

        DatabaseManager db = new DatabaseManager(this);
        boolean bool = db.AddUser(username,gender,age);

        Intent i = getIntent();
        i.putExtra(MainActivity.EXTRA_MESSAGE, username);
        setResult(Activity.RESULT_OK, i);
        finish();
    }
}