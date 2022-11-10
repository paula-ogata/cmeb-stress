package org.jacmeb.ecgapp_second;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.TextView;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class PlotActivity extends AppCompatActivity {
    String fName;
    ArrayList<Integer> values = new ArrayList<>();
    ArrayList<Double> xTime = new ArrayList<>();
    ArrayList<Integer> baseLine = new ArrayList<>();
    AssetManager asMan;
    PointF minXY, maxXY;
    Number minY, maxY;
    float FREQ = 200.0f;
    float PERIOD = 1/FREQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);

        fName = "ecgFiles/" + getIntent().getStringExtra(MainActivity.ID_EXTRA);
        try {
            ReadValues();
            PlotValues();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

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
            baseLine.add(0);
            newX += (double)PERIOD;
        }
    }

    public void PlotValues() throws InterruptedException {
        XYSeries seriesECG = new SimpleXYSeries(xTime, values, "ECG data");
        XYSeries seriesBL = new SimpleXYSeries(xTime, baseLine, "Base Line");

        LineAndPointFormatter seriesFormatECG = new LineAndPointFormatter(Color.BLUE, null, null, null);
        LineAndPointFormatter seriesFormatBL = new LineAndPointFormatter(Color.RED, null, null, null);

        com.androidplot.xy.XYPlot plot = (com.androidplot.xy.XYPlot)findViewById(R.id.plot);

        //Range = y ___ Domain = x
        double xMin = Collections.min(xTime);
        double xMax = Collections.max(xTime);
        double yMin = Collections.min(values);
        double yMax = Collections.max(values);

        plot.setDomainBoundaries(xMin, xMax, BoundaryMode.FIXED);
        plot.setRangeBoundaries(yMin, yMax, BoundaryMode.FIXED);

        plot.addSeries(seriesECG,seriesFormatECG);
        plot.addSeries(seriesBL, seriesFormatBL);

        plot.getGraph().setMarginLeft((float)75);

        PanZoom.attach(plot);
    }
}