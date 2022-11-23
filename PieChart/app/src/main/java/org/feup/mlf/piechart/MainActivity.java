package org.feup.mlf.piechart;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Color;
import android.widget.TextView;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class MainActivity extends AppCompatActivity {

    // Create the object of TextView and PieChart class
    TextView tvRelaxado, tvLeve, tvAlto, tvModerado;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link those objects with their respective
        // id's that we have given in .XML file
        tvRelaxado = findViewById(R.id.tvRelaxado);
        tvLeve = findViewById(R.id.tvLeve);
        tvAlto = findViewById(R.id.tvAlto);
        tvModerado = findViewById(R.id.tvModerado);
        pieChart = findViewById(R.id.piechart);

        // Creating a method setData()
        // to set the text in text view and pie chart
        setData();
    }

    private void setPieChartData() {
        // Set the percentage of language used
        tvRelaxado.setText(Integer.toString(40));
        tvLeve.setText(Integer.toString(30));
        tvModerado.setText(Integer.toString(5));
        tvAlto.setText(Integer.toString(25));

        // Set the data and color to the pie chart
        pieChart.addPieSlice(
                new PieModel(
                        "Relaxado",
                        Integer.parseInt(tvRelaxado.getText().toString()),
                        Color.parseColor("#29B6F6")));
        pieChart.addPieSlice(
                new PieModel(
                        "Leve",
                        Integer.parseInt(tvLeve.getText().toString()),
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "Moderado",
                        Integer.parseInt(tvModerado.getText().toString()),
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "Alto",
                        Integer.parseInt(tvAlto.getText().toString()),
                        Color.parseColor("#EF5350")));

        // To animate the pie chart
        pieChart.startAnimation();

    }
}