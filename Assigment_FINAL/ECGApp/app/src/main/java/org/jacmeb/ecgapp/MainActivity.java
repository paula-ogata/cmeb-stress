package org.jacmeb.ecgapp;

/**
 *
 * <h1>Android application that plots an ECG file.</h1>
 * The user can select one of two files to plot in the phone.
 * This project was developed as an assignment for the CMEB class, presented by FEUP.
 * @author João Carvalho
 * @author José Almeida
 * @author Manuel Fortunato
 * @author Paula Ogata
 *
 */

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 *
 * This is the initial activity, where the user will be prompt with the possibility to choose
 * between two files (each one having a specific select button) and a start button that activates
 * the PlotActivity.java, where the selected file is plotted.
 * In case the Application is unable to find any files inside the "assets/ecgFiles" folder, the start
 * button is hidden and the other two buttons display an error message in the form of a toast
 * message.
 *
 */

public class MainActivity extends AppCompatActivity {

    /**
     * Used Variables:
     *  String ID_EXTRA: name for the putExtra method
     *  AssetManager asMan: asset manager object to access the files
     *  String[] fNames: string with the names of the ECG files
     *  Button bt1, bt2, startBt: Select and start buttons
     *  boolean worked: boolean that specifies if the program could access the files (true) or not
     *      (false)
     *  int file_num: specifies which files was selected, the first one (=0) or the second one (=1)
     *      or if none was selected (=-1)
     */
    public final static String ID_EXTRA = "org.jacmeb.ecgApp.FILE";
    AssetManager asMan;
    String[] fNames;
    Button bt1, bt2, startBt;
    boolean worked = false;
    int file_num = -1;

    /**
     * This is the main method, where the layout is associated to the activity. It also tries to
     * access the files in "assets/ecgFiles" and get their names. If it successes, then gives the
     * names of the files to each of the two select buttons and enables listeners to the two select
     * buttons and the start button.
     * If the program can not access the folder, then it hides the start button and defines the text
     * of the select buttons as "N/A". If the user tries to click them, the background does not
     * change but triggers an error toast message.
     *
     * @param savedInstanceState Bundle object to retrieve information about previous activities
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        if(bar!=null) {
            bar.hide();
        }

        asMan = getAssets();
        try {
            fNames = asMan.list("ecgFiles");
            worked = true;
        } catch (Exception ex) {
            worked = false;
        }

        bt1 = (Button) findViewById(R.id.ecg1_bt);
        bt2 = (Button) findViewById(R.id.ecg2_bt);
        startBt = (Button) findViewById(R.id.start_bt);

        if(worked) {
            startBt.setVisibility(View.VISIBLE);
            bt1.setText(fNames[0]);
            bt2.setText(fNames[1]);
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(),"ERROR: No files available",Toast. LENGTH_SHORT);
            toast.show();
            startBt.setVisibility(View.GONE);
            bt1.setText("N/A");
            bt2.setText("N/A");
        }

        bt1.setOnClickListener((View v)->onBtB1Click());
        bt2.setOnClickListener((View v)->onBtB2Click());
        startBt.setOnClickListener((View v)->onBtStartClick());
    }

    /**
     * Listener to the first select button. If the program could access the folder and the button is
     * click, then defines the respective file as the select one, changes the color of its background
     * and the color of the other button to the base one, in case the user select the other one first
     * and then changed it's mind.
     * If the program could not access the folder, then everytime the user clicks the button it shows
     * and error toast message.
     */
    void onBtB1Click() {
        if(!worked) {
            Toast toast = Toast.makeText(getApplicationContext(),"ERROR: No files available",Toast. LENGTH_SHORT);
            toast.show();
        } else {
            file_num = 0;
            bt1.setBackgroundColor(Color.parseColor("#CDE53935"));
            bt2.setBackgroundColor(Color.parseColor("#1976D2"));
        }
    }

    /**
     * Listener to the second select button. Does the same as the previous listener but to the second
     * button.
     */
    void onBtB2Click() {
        if(!worked) {
            Toast toast = Toast.makeText(getApplicationContext(),"ERROR: No files available",Toast. LENGTH_SHORT);
            toast.show();
        } else {
            file_num = 1;
            bt2.setBackgroundColor(Color.parseColor("#CDE53935"));
            bt1.setBackgroundColor(Color.parseColor("#1976D2"));
        }
    }

    /**
     * Listener to the start button. If one of the select button was clicked, then this listener
     * creates an Intent to start the PlotActivity and shares the name of the file selected to it.
     * If no select button was clicked, then it shows a toast message saying it.
     * If the program could not access the ECG folder, then this button is hidden and this listener
     * is never triggered.
     */
    void onBtStartClick() {
        if (file_num != -1) {
            Intent i = new Intent(this, PlotActivity.class);
            String fileName = fNames[file_num];
            i.putExtra(ID_EXTRA, fileName);
            startActivity(i);
        } else {
            Toast.makeText(this, "No item Selected", Toast.LENGTH_LONG).show();
        }
    }
}