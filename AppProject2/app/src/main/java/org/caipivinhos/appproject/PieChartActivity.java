package org.caipivinhos.appproject;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    SharedPreferences sharedpreferences;
    PieChart pieChart;
    EditText commentReport;
    String date;
    Double mediumLevel;
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
    boolean serviceRunning = false;
    String simulatedData = "simulatedData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        ActionBar bar = getSupportActionBar();

        if (bar != null) {
            bar.setIcon(R.drawable.icon);
            bar.setTitle("BeCalm");
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            date = getIntent().getStringExtra(GET_DATE);
        } else {
            Date time = new Date();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(time);
            date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
        }

        // Link those objects with their respective
        // id's that we have given in .XML file
        db = new DatabaseManager(this);
        tvModerado = findViewById(R.id.tvModerado);
        tvAlto = findViewById(R.id.tvAlto);
        tvSevero = findViewById(R.id.tvSevero);
        pieChart = findViewById(R.id.piechart);
        commentReport = findViewById(R.id.commentReport);
        submitComment = findViewById(R.id.submitComment);
        sharedpreferences = getSharedPreferences("App", MODE_PRIVATE);


        dateBt = findViewById(R.id.buttonDate);
        dateBt.setText(date);


        // feelings buttons
        btHappy = findViewById(R.id.bt_feel1);
        btTired = findViewById(R.id.bt_feel2);
        btSad = findViewById(R.id.bt_feel3);
        btIndifferent = findViewById(R.id.bt_feel5);
        btIrritable = findViewById(R.id.bt_feel4);
        btAnx = findViewById(R.id.bt_feel6);

        btHappy.setOnClickListener((View v) -> onBtHappyClick());
        btTired.setOnClickListener((View v) -> onBtTiredClick());
        btSad.setOnClickListener((View v) -> onBtSadClick());
        btIndifferent.setOnClickListener((View v) -> onBtIndClick());
        btIrritable.setOnClickListener((View v) -> onBtIrrClick());
        btAnx.setOnClickListener((View v) -> onBtAnxClick());

        submitComment.setOnClickListener(view -> {
            if (!isHappy && !isAnx && !isIndifferent && !isIrritable && !isSad && !isTired) {
                Toast.makeText(this, "Please select your mood first", Toast.LENGTH_LONG).show();
            } else {
                int happy = isHappy? 1 : 0;
                int anx = isAnx? 1 : 0;
                int ind = isIndifferent? 1 : 0;
                int irrit = isIrritable? 1 : 0;
                int sad = isSad? 1 : 0;
                int tired = isTired? 1 : 0;
                String mood = happy + "_" + anx + "_" +
                        ind + "_" + irrit + "_" +
                        sad + "_" + tired;

                String comment = commentReport.getText().toString();
                if(comment.equals("")) {
                    comment = "NULL";
                }

                db.AddComment(mood, comment, date);
                Toast.makeText(this, "Comment registered.", Toast.LENGTH_LONG).show();
            }
        });

        //method for Date selection
        dateBt.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });

        mediumLevel = db.getMediumLevel();
        startStopAcquisition = findViewById(R.id.startStopAcquisition);

        startStopAcquisition.setOnClickListener(this::onBtStartStopClick);

        getReportComment(date);
        setPieChartData();
    }

    private void setPieChartData() {
        // Set the percentage of language used
        if (!sharedpreferences.getBoolean(simulatedData, false)) {
            db.simulateData();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(simulatedData, Boolean.TRUE);
            editor.apply();
        }

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

        String ModeratePercentage = (percentages[0] + 1) + "%";
        String HighPercentage = percentages[1] + "%";
        String SeverePercentage = percentages[2] + "%";

        tvModerado.setText(ModeratePercentage);
        tvAlto.setText(HighPercentage);
        tvSevero.setText(SeverePercentage);

        // Set the data and color to the pie chart
        pieChart.clearChart();
        pieChart.addPieSlice(
                new PieModel(
                        "Moderate",
                        Integer.parseInt(ModeratePercentage.split("%")[0]),
                        Color.parseColor("#29B6F6")));
        pieChart.addPieSlice(
                new PieModel(
                        "High",
                        Integer.parseInt(HighPercentage.split("%")[0]),
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Severe",
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
            Intent i = new Intent(this, BarChartActivity.class);
            i.putExtra(GET_DATE, date);
            startActivity(i);
            return (true);
        } else if (item.getItemId() == R.id.chartWeek) {
            startActivity(new Intent(this, BarChartActivityWeek.class));
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
        String[] state = db.GetComment(date);
        String comment = null;
        if(state!=null) {
            if(state[0]!=null) {
                String moodString = state[0];
                String[] moods = moodString.split("_");

                if(Integer.parseInt(moods[0]) == 1) {
                    isHappy = true;
                    btHappy.setBackgroundColor(Color.parseColor("#FFB6AF"));
                    btHappy.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    btHappy.setTextColor(Color.parseColor("#FFB6AF"));
                    btHappy.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    isHappy = false;
                }

                if(Integer.parseInt(moods[1]) == 1) {
                    isAnx = true;
                    btAnx.setBackgroundColor(Color.parseColor("#FFB6AF"));
                    btAnx.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    btAnx.setTextColor(Color.parseColor("#FFB6AF"));
                    btAnx.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    isAnx = false;
                }

                if(Integer.parseInt(moods[2]) == 1) {
                    isIndifferent = true;
                    btIndifferent.setBackgroundColor(Color.parseColor("#FFB6AF"));
                    btIndifferent.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    btIndifferent.setTextColor(Color.parseColor("#FFB6AF"));
                    btIndifferent.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    isIndifferent = false;
                }

                if(Integer.parseInt(moods[3]) == 1) {
                    isIrritable = true;
                    btIrritable.setBackgroundColor(Color.parseColor("#FFB6AF"));
                    btIrritable.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    btIrritable.setTextColor(Color.parseColor("#FFB6AF"));
                    btIrritable.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    isIrritable = false;
                }

                if(Integer.parseInt(moods[4]) == 1) {
                    isSad = true;
                    btSad.setBackgroundColor(Color.parseColor("#FFB6AF"));
                    btSad.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    btSad.setTextColor(Color.parseColor("#FFB6AF"));
                    btSad.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    isSad = false;
                }

                if(Integer.parseInt(moods[5]) == 1) {
                    isTired = true;
                    btTired.setBackgroundColor(Color.parseColor("#FFB6AF"));
                    btTired.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    btTired.setTextColor(Color.parseColor("#FFB6AF"));
                    btTired.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    isTired = false;
                }
            } else {
                btHappy.setTextColor(Color.parseColor("#FFB6AF"));
                btHappy.setBackgroundColor(Color.parseColor("#FFFFFF"));
                isHappy = false;

                btAnx.setTextColor(Color.parseColor("#FFB6AF"));
                btAnx.setBackgroundColor(Color.parseColor("#FFFFFF"));
                isAnx = false;

                btIndifferent.setTextColor(Color.parseColor("#FFB6AF"));
                btIndifferent.setBackgroundColor(Color.parseColor("#FFFFFF"));
                isIndifferent = false;

                btIrritable.setTextColor(Color.parseColor("#FFB6AF"));
                btIrritable.setBackgroundColor(Color.parseColor("#FFFFFF"));
                isIrritable = false;

                btSad.setTextColor(Color.parseColor("#FFB6AF"));
                btSad.setBackgroundColor(Color.parseColor("#FFFFFF"));
                isSad = false;

                btTired.setTextColor(Color.parseColor("#FFB6AF"));
                btTired.setBackgroundColor(Color.parseColor("#FFFFFF"));
                isTired = false;
            }

            if(state[1] != null) {
                if(!state[1].equals("NULL")) {
                    comment = state[1];
                }
            }
        } else {
            btHappy.setTextColor(Color.parseColor("#FFB6AF"));
            btHappy.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isHappy = false;

            btAnx.setTextColor(Color.parseColor("#FFB6AF"));
            btAnx.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isAnx = false;

            btIndifferent.setTextColor(Color.parseColor("#FFB6AF"));
            btIndifferent.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isIndifferent = false;

            btIrritable.setTextColor(Color.parseColor("#FFB6AF"));
            btIrritable.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isIrritable = false;

            btSad.setTextColor(Color.parseColor("#FFB6AF"));
            btSad.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isSad = false;

            btTired.setTextColor(Color.parseColor("#FFB6AF"));
            btTired.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isTired = false;
        }

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

    public void onBtStartStopClick(View view) {

        if (!VitalJacketManager.checkIfConnected()) {
            Toast.makeText(this, "Please Connect To VitalJacket First", Toast.LENGTH_LONG).show();
        } else {
            Intent ServiceIntent = new Intent(this, MyBackgroundService.class);
            if (!serviceRunning) {
                startStopAcquisition.setText("Stop Acquisition");
                // Start Background Service
                //Intent ServiceIntent = new Intent(this, MyBackgroundService.class);
                serviceRunning = true;
                startService(ServiceIntent);
            } else {
                startStopAcquisition.setText("Start Acquisition");
                // Stop service
                //Intent ServiceIntent = new Intent(this, MyBackgroundService.class);
                serviceRunning = false;
                stopService(ServiceIntent);
            }
        }
    }
}