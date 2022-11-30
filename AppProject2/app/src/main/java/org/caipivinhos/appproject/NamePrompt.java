package org.caipivinhos.appproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NamePrompt extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_prompt);
        findViewById(R.id.button).setOnClickListener(this::btSendOnClick);
    }

    public void btSendOnClick(View view){
        EditText editText = findViewById(R.id.txtUsername);
        String username = editText.getText().toString();
        Intent i = getIntent();
        i.putExtra(MainActivity.EXTRA_MESSAGE, username);
        setResult(Activity.RESULT_OK, i);
        finish();
    }


}