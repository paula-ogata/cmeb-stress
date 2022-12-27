package org.caipivinhos.appproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class ChooseBTDevice extends AppCompatActivity {

    private ArrayAdapter<String> listAdapter;
    private String macAddress = "";
    private static final String TAG = "ChooseBTDevice";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_btdevice);

        ActionBar bar = getSupportActionBar();
        if (bar != null){
            bar.setIcon(R.drawable.icon);
            bar.setTitle("BeCalm");
        }

        Button buttonOK = findViewById(R.id.cmdOK);
        buttonOK.setOnClickListener(view -> {
            VitalJacketManager.setMacAddress(macAddress);
            try {
                //VitalJacketManager.connectToVJ(this);
                Toast.makeText(this, "Connect", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.d(TAG, "onCreate: " + e.getMessage());
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            }
        });

        try {
            ListView mainListView = findViewById(R.id.lstDevices);

            ArrayList<String> lstDevices = new ArrayList<>();

            // Create ArrayAdapter using the planet list.
            listAdapter = new ArrayAdapter<>(this, R.layout.simplerow, lstDevices);

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter != null) {
                if (mBluetoothAdapter.isEnabled()) {
                    Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
                    for (BluetoothDevice device : devices)
                    {
                        listAdapter.add(device.getAddress() + "   " + device.getName());
                    }
                }
            }
            mainListView.setAdapter( listAdapter );

            mainListView.setOnItemClickListener((parent, item, position, id) -> {
                mainListView.setSelected(true);
                macAddress = (String) listAdapter.getItem(position);
                String[] aux = macAddress.split("   ");
                macAddress = aux[0];
            });
        }
        catch (SecurityException exception)
        {
            buttonOK.setText("N/A");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.main, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            startActivity(new Intent(this, PieChartActivity.class));
            return(true);
        }
        else if (item.getItemId()==R.id.chart) {
            startActivity(new Intent(this, ChooseBTDevice.class));
            return(true);
        }
        else if (item.getItemId()==R.id.chartWeek) {
            startActivity(new Intent(this, BarChartActivityWeek.class));
            return(true);
        }
        else if(item.getItemId()==R.id.chooseBt) {
            String message = "You're already at Choose Device page";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
}