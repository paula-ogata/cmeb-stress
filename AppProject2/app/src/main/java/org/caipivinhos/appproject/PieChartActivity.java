package org.caipivinhos.appproject;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.text.DateFormat;
import java.util.Calendar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class PieChartActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    // Create the object of TextView and PieChart class
    TextView tvModerado, tvAlto, tvSevero;
    PieChart pieChart;
    EditText commentReport;
    String date = "13/12/2022";
    Button submitComment;
    DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        // Link those objects with their respective
        // id's that we have given in .XML file
        db = new DatabaseManager(this);
        tvModerado = findViewById(R.id.tvModerado);
        tvAlto = findViewById(R.id.tvAlto);
        tvSevero = findViewById(R.id.tvSevero);
        pieChart = findViewById(R.id.piechart);
        commentReport = findViewById(R.id.commentReport);
        submitComment = findViewById(R.id.submitComment);

        // Creating a method setData()
        // to set the text in text view and pie chart
        getReportComment(date);
        setPieChartData();

        submitComment.setOnClickListener(view -> {
            String comment = commentReport.getText().toString();
            if(!comment.equals("")){
                db.AddComment(comment, date);
                Toast.makeText(this, "Comment registered.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Please add a comment first", Toast.LENGTH_LONG).show();
            }
        });

        //method for Date selection
        Button button = (Button) findViewById(R.id.buttonDate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    private void setPieChartData() {
        // Set the percentage of language used
        DatabaseManager db = new DatabaseManager(this);
        //db.simulateData();
        int[] stressLevels = db.getStressLevelsPieChart(date);

        tvModerado.setText(String.valueOf(stressLevels[0]));
        tvAlto.setText(String.valueOf(stressLevels[1]));
        tvSevero.setText(String.valueOf(stressLevels[2]));

        // Set the data and color to the pie chart
        pieChart.addPieSlice(
                new PieModel(
                        "Moderado",
                        Integer.parseInt(tvModerado.getText().toString()),
                        Color.parseColor("#29B6F6")));
        pieChart.addPieSlice(
                new PieModel(
                        "Alto",
                        Integer.parseInt(tvAlto.getText().toString()),
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Severo",
                        Integer.parseInt(tvSevero.getText().toString()),
                        Color.parseColor("#EF5350")));

        // To animate the pie chart
        pieChart.startAnimation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            String message = "Already in home - FOLEIRO MUDAR";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
            startActivity(new Intent(this, InstantAcquisition.class));
            return(true);
        }
        return (super.onOptionsItemSelected(item));
    }

    public void getReportComment(String date) {
        String comment = db.GetComment(date);
        if(comment != null) {
            commentReport.setText(comment);
        }
    }

    //method for Date pick
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        //c.get(Calendar.YEAR)
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        //String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        String currentDateString = c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
        TextView textView = (TextView) findViewById(R.id.textViewDate);
        textView.setText(currentDateString);
    }
}