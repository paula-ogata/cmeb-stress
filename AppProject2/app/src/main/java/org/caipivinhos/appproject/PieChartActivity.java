package org.caipivinhos.appproject;



import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class PieChartActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    TextView tvModerado, tvAlto, tvSevero;
    PieChart pieChart;
    EditText commentReport;
    String date;
    Button submitComment, dateBt, startStopAcquisition;
    Button btHappy, btTired, btSad, btIndifferent, btIrritable, btAnx;
    DatabaseManager db;
    private static final String TAG = "PieChartActivity";
    String GET_DATE = "Date_Intent_Info";
    boolean isHappy = false;
    boolean isTired = false;
    boolean isSad = false;
    boolean isIndifferent = false;
    boolean isAnx = false;
    boolean isIrritable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        ActionBar bar = getSupportActionBar();

        if (bar != null) {
            bar.setIcon(R.drawable.icon);
            bar.setTitle("BeCalm");
        }

        Date time = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(time);
        date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);


        // Link those objects with their respective
        // id's that we have given in .XML file
        db = new DatabaseManager(this);
        tvModerado = findViewById(R.id.tvModerado);
        tvAlto = findViewById(R.id.tvAlto);
        tvSevero = findViewById(R.id.tvSevero);
        pieChart = findViewById(R.id.piechart);
        commentReport = findViewById(R.id.commentReport);
        submitComment = findViewById(R.id.submitComment);


        dateBt = findViewById(R.id.buttonDate);

        dateBt.setText(date);
        getReportComment(date);
        setPieChartData();

        // feelings buttons
        btHappy = findViewById(R.id.bt_feel1);
        btTired = findViewById(R.id.bt_feel2);
        btSad = findViewById(R.id.bt_feel3);
        btIndifferent = findViewById(R.id.bt_feel5);
        btIrritable = findViewById(R.id.bt_feel4);
        btAnx = findViewById(R.id.bt_feel6);

        btHappy.setOnClickListener((View v)-> onBtHappyClick());
        btTired.setOnClickListener((View v)-> onBtTiredClick());
        btSad.setOnClickListener((View v)-> onBtSadClick());
        btIndifferent.setOnClickListener((View v)-> onBtIndClick());
        btIrritable.setOnClickListener((View v)-> onBtIrrClick());
        btAnx.setOnClickListener((View v)-> onBtAnxClick());

        submitComment.setOnClickListener(view -> {
            String comment = commentReport.getText().toString();
            if (!comment.equals("")) {
                db.AddComment(comment, date);
                Toast.makeText(this, "Comment registered.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Please add a comment first", Toast.LENGTH_LONG).show();
            }
        });

        //method for Date selection
        dateBt.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });


        startStopAcquisition = findViewById(R.id.startStopAcquisition);
        if (isLongSessionConnected() == false) {
            Toast.makeText(this, "Please Connect To VitalJacket First", Toast.LENGTH_LONG).show();
        } else {
            startStopAcquisition.setOnClickListener(view -> onBtStartStopClick());
        }


    }

    private void setPieChartData() {
        // Set the percentage of language used
        db.simulateData();
        int[] stressLevels = db.getStressLevelsPieChart(date);

        double sumValues = 0;
        for (int stressLevel : stressLevels) {
            sumValues += stressLevel;
        }

        int[] percentages = new int[stressLevels.length];
        if (sumValues != 0) {
            for (int i = 0; i < stressLevels.length; i++) {
                percentages[i] = (int) (((double) stressLevels[i] / sumValues) * 100);
            }
        }

        String ModeratePercentage = (percentages[0] + 1) +"%";
        String HighPercentage = percentages[1] +"%";
        String SeverePercentage = percentages[2] +"%";

        tvModerado.setText(ModeratePercentage);
        tvAlto.setText(HighPercentage);
        tvSevero.setText(SeverePercentage);

        // Set the data and color to the pie chart
        pieChart.clearChart();
        pieChart.addPieSlice(
                new PieModel(
                        "Moderado",
                        Integer.parseInt(ModeratePercentage.split("%")[0]),
                        Color.parseColor("#29B6F6")));
        pieChart.addPieSlice(
                new PieModel(
                        "Alto",
                        Integer.parseInt(HighPercentage.split("%")[0]),
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Severo",
                        Integer.parseInt(SeverePercentage.split("%")[0]),
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
            String message = "You're already at Home Page";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else if (item.getItemId() == R.id.chart) {
            startActivity(new Intent(this, BarChartActivity.class));
            return (true);
        } else if (item.getItemId() == R.id.chooseBt) {
            startActivity(new Intent(this, ChooseBTDevice.class));
            return (true);
        } else if (item.getItemId() == R.id.about) {
            startActivity(new Intent(this, AboutActivity.class));
            return (true);
        } else if (item.getItemId() == R.id.hiw) {
            startActivity(new Intent(this, HowWorksActivity.class));
            return (true);
        } else if (item.getItemId() == R.id.instant) {
            startActivity(new Intent(this, InstantAcquisition.class));
            return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    public void onBtHappyClick() {
        if (isHappy) {
            btHappy.setTextColor(Color.parseColor("#FFB6AF"));
            btHappy.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isHappy = false;
        } else {
            btHappy.setBackgroundColor(Color.parseColor("#FFB6AF"));
            btHappy.setTextColor(Color.parseColor("#FFFFFF"));
            isHappy = true;
        }
    }

    public void onBtTiredClick() {
        if (isTired) {
            btTired.setTextColor(Color.parseColor("#FFB6AF"));
            btTired.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isTired = false;
        } else {
            btTired.setBackgroundColor(Color.parseColor("#FFB6AF"));
            btTired.setTextColor(Color.parseColor("#FFFFFF"));
            isTired = true;
        }
    }

    public void onBtSadClick() {
        if (isSad) {
            btSad.setTextColor(Color.parseColor("#FFB6AF"));
            btSad.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isSad = false;
        } else {
            btSad.setBackgroundColor(Color.parseColor("#FFB6AF"));
            btSad.setTextColor(Color.parseColor("#FFFFFF"));
            isSad = true;
        }
    }

    public void onBtIndClick() {
        if (isIndifferent) {
            btIndifferent.setTextColor(Color.parseColor("#FFB6AF"));
            btIndifferent.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isIndifferent = false;
        } else {
            btIndifferent.setBackgroundColor(Color.parseColor("#FFB6AF"));
            btIndifferent.setTextColor(Color.parseColor("#FFFFFF"));
            isIndifferent = true;
        }
    }

    public void onBtAnxClick() {
        if (isAnx) {
            btAnx.setTextColor(Color.parseColor("#FFB6AF"));
            btAnx.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isAnx = false;
        } else {
            btAnx.setBackgroundColor(Color.parseColor("#FFB6AF"));
            btAnx.setTextColor(Color.parseColor("#FFFFFF"));
            isAnx = true;
        }
    }

    public void onBtIrrClick() {
        if (isIrritable) {
            btIrritable.setTextColor(Color.parseColor("#FFB6AF"));
            btIrritable.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isIrritable = false;
        } else {
            btIrritable.setBackgroundColor(Color.parseColor("#FFB6AF"));
            btIrritable.setTextColor(Color.parseColor("#FFFFFF"));
            isIrritable = true;
        }
    }

    public void getReportComment(String date) {
        String comment = db.GetComment(date);
        commentReport.setText(comment);
    }


    //method for Date pick
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = GregorianCalendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        date = c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        Log.d(TAG, "onDateSet: date " + date);
        dateBt.setText(date);
        getReportComment(date);
        setPieChartData();
    }

    public void onBtStartStopClick() {
        startStopAcquisition = findViewById(R.id.startStopAcquisition);
        Intent ServiceIntent = new Intent(this, MyBackgroundService.class);

        if (startStopAcquisition.getText().equals("Start Acquisition")) {
            startStopAcquisition.setText("Stop Acquisition");
            // Start Background Service
            //Intent ServiceIntent = new Intent(this, MyBackgroundService.class);
            startService(ServiceIntent);
        } else {
            startStopAcquisition.setText("Start Acquisition");
            // Stop service
            //Intent ServiceIntent = new Intent(this, MyBackgroundService.class);
            stopService(ServiceIntent);
        }
    }



    public boolean isLongSessionConnected() {

        // Iniciar variaveis aqui (localmente) é conflituoso?
        DatabaseManager db = null;
        double mediumLevel;
        db = new DatabaseManager(this);
        mediumLevel = db.getMediumLevel();
        Context context;
        context = this;

        if (VitalJacketManager.longSession(this, mediumLevel) == -1) {
            return false;
        } else {
            return true;
        }

    }
}