package org.jacmeb.ecgapp;

/**
 *
 * <h1>Android application that plots an ECG file.</h1>
 * The user can select one of two files to plot in the phone.
 * This project was developed as an assignment for the CMEB class, presented by FEUP.
 *
 * @author João Carvalho
 * @author José Almeida
 * @author Manuel Fortunato
 * @author Paula Ogata
 *
 */

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * This is the plot Activity. This activity is activated by the main one. It receives the information
 * about which ECG file was selected and plots it, using AndroidPlot XY.
 *
 */

public class PlotActivity extends AppCompatActivity {

    /**
     * Used Variables:
     *  String file: name of the file selected in the previous activity
     *  String fName: path to the ECG file
     *  int windowSize: window size for the smoothing of the ECG
     *  ArrayList< Integer> values: values of the ECG file
     *  ArrayList< Double> xTime: represents the time for each point
     *  ArrayList< Integer> baseLine: smooth result of the ECG
     *  ArrayList< Integer> zeroLine: Reference (all points equal to zero)
     *  AssetManager asMan: asset manager object to access the files
     *  float FREQ: frequency
     *  float PERIOD: period
     *  com.androidplot.xy.XYPlot plot: plot object to be displayed
     */
    String file;
    String fName;
    int windowSize = 200;
    ArrayList<Integer> values = new ArrayList<>();
    ArrayList<Double> xTime = new ArrayList<>();
    ArrayList<Integer> baseLine = new ArrayList<>();
    ArrayList<Integer> zeroLine = new ArrayList<>();
    AssetManager asMan;;
    float FREQ = 200.0f;
    float PERIOD = 1/FREQ;
    com.androidplot.xy.XYPlot plot;

    /**
     * This is the main method of this activity. It gets the information about the file selected by
     * accessing it through the intent that activated the activity. Then tries to read the file and
     * plot it.
     * @param savedInstanceState Bundle object to retrieve information about previous activities
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);

        ActionBar bar = getSupportActionBar();
        if(bar!=null) {
            bar.hide();
        }

        file = getIntent().getStringExtra(MainActivity.ID_EXTRA);
        fName = "ecgFiles/" + file;

        try {
            ReadValues();
            PlotValues();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method tries to read the values of the selected ECG file and saves the values to an
     * ArrayList.
     * It was developed based on the hint provided by the professors of the class.
     * It also establish a baseLine and a Reference (All points equals to 0) array also to be plotted
     * and help the analyses of the ECG plot.
     * The points added to the xTime array are based on the PERIOD specified. This way, the xx axis is
     * correctly associated to the yy axis.
     * @throws IOException class for exceptions thrown while accessing information using streams, files and directories.
     */
    public void ReadValues() throws IOException {
        String line;
        asMan = getAssets();

        InputStream inS = asMan.open(fName);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inS));
        line = reader.readLine();
        while (line!=null) {
            values.add(Integer.valueOf(line));
            line = reader.readLine();
        }

        double newX = 0;
        for(int i= 0; i<values.size(); i++) {
            xTime.add(newX);
            zeroLine.add(0);
            newX += (double)PERIOD;
        }

        baseLine = smooth(values, windowSize);
    }


    /**
     * This method plots both ECG and baseline values saved in ArrayLists.
     * It uses XYSeries to create the series and LineAndPointFormatter to format each series. The ECG
     * is plotted in color and the baseline in red.
     * The title for the plot is defined based on the file name, deleting the '.txt.' extension of the
     * name.
     * The axis is defined based on the maximum an minimum of the ECG values, for both x and y.
     * A PanZoom object is also attach to the plot, so that the user can manipulate it (zoom in and
     * out or move left, right, up and down)
     * @throws InterruptedException thrown when a thread is waiting, sleeping, or otherwise occupied,
     *         and the thread is interrupted, either before or during the activity.
     */
    public void PlotValues() throws InterruptedException {
        XYSeries seriesECG = new SimpleXYSeries(xTime, values, "ECG");
        XYSeries seriesBL = new SimpleXYSeries(xTime, baseLine, "BL");
        XYSeries seriesZero = new SimpleXYSeries(xTime, zeroLine, "Ref.");

        LineAndPointFormatter seriesFormatECG = new LineAndPointFormatter(Color.BLUE, null, null, null);
        LineAndPointFormatter seriesFormatBL = new LineAndPointFormatter(Color.RED, null, null, null);
        LineAndPointFormatter seriesFormatZero = new LineAndPointFormatter(Color.GREEN, null, null, null);

        plot = (com.androidplot.xy.XYPlot)findViewById(R.id.plot);
        plot.setTitle(file.toUpperCase().replace(".TXT","") + " plot");

        //Range = y ___ Domain = x
        double xMin = Collections.min(xTime);
        double xMax = Collections.max(xTime);
        double yMin = Collections.min(values);
        double yMax = Collections.max(values);

        plot.setDomainBoundaries(xMin, xMax, BoundaryMode.FIXED);
        plot.setRangeBoundaries(yMin, yMax, BoundaryMode.FIXED);

        plot.addSeries(seriesBL, seriesFormatBL);
        plot.addSeries(seriesECG,seriesFormatECG);
        plot.addSeries(seriesZero, seriesFormatZero);

        PanZoom.attach(plot);
    }

    /**
     * This method works as a smooth window to help create the baseline for the ECG file.
     * @param dataList data from the ECG
     * @param windowSize size of the smooth window
     * @return An ArrayList with the result of the application of the smooth window to the ECG
     */
    public ArrayList<Integer> smooth(ArrayList<Integer> dataList, int windowSize)
    {
        int[] data = new int[dataList.size()];
        int _i = 0;
        for (int point : dataList) {
            data[_i++] = point;
        }
        int n = data.length;
        int width = Math.min(windowSize, n);

        width = width - 1 + (width % 2);


        int[] buffer = new int[width];
        int[] output = new int[n];
        int current_index = 0;
        for (int i = 0; i < n; i++)
        {
            buffer[current_index] = data[i] / width;
            int ma = 0;
            for (int j = 0; j < windowSize -1 ; j++)
            {
                ma += buffer[j];
            }
            output[i] = ma;
            current_index = (current_index + 1) % width;
        }

        int[] cBeginBase = new int[width];
        int ncBB = cBeginBase.length;
        int cumSum = 0;
        for (int i = 0; i < ncBB; i++)
        {
            cumSum += output[i];
            cBeginBase[i] = cumSum;
        }
        int[] cBegin = new int[(width - 1) / 2];
        int ncB = cBegin.length;
        for (int i = 0; i < ncB; i++)
        {
            cBegin[i] = (int)(cBeginBase[i * 2] / ((i + 1) * 2 - 1));
        }

        int[] cEndBase = new int[width];
        int nEB = cEndBase.length;
        cumSum = 0;
        for (int i = 0; i < nEB; i++)
        {
            cumSum += output[n - i - 1];
            cEndBase[i] = cumSum;
        }
        int[] cEnd = new int[(width - 1) / 2];
        int nE = cEnd.length;
        int pos;
        for (int i = 0; i < nE; i++)
        {
            pos = nEB - 1 - i * 2;
            cEnd[i] = cEndBase[pos] / (nEB - i * 2);
        }

        int[] usedOutput = new int[n - width];
        int nUO = usedOutput.length;
        System.arraycopy(output, width , usedOutput, 0, nUO);

        int[] result = new int[n];
        System.arraycopy(cBegin, 0, result, 0, cBegin.length);
        System.arraycopy(usedOutput, 0, result, cBegin.length, usedOutput.length);
        System.arraycopy(cEnd, 0, result, cBegin.length+ usedOutput.length, cEnd.length);

        ArrayList<Integer> resultList = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            resultList.add(result[i]);
        }

        return resultList;
    }
}