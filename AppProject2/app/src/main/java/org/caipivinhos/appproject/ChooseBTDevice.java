package org.caipivinhos.appproject;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
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

    public static String SELECT_DEVICE_ADDRESS = "device_address";
    private ArrayAdapter<String> listAdapter;
    private String selectedValue = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_btdevice);

        Button buttonOK = findViewById(R.id.cmdOK);
        buttonOK.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(SELECT_DEVICE_ADDRESS, selectedValue);
            startActivity(intent);
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
                selectedValue = (String) listAdapter.getItem(position);
                String[] aux = selectedValue.split("   ");
                selectedValue = aux[0];
            });
        }
        catch (SecurityException exception)
        {
            buttonOK.setText("N/A");
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            startActivity(new Intent(this, PieChartActivity.class));
            return(true);
        }
        else if (item.getItemId()==R.id.chart) {
            startActivity(new Intent(this, BarChartActivity.class));
            return(true);
        }
        else if(item.getItemId()==R.id.chooseBt) {
            String message = "Already in home - FOLEIRO MUDAR";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        else if (item.getItemId()==R.id.about){
            startActivity(new Intent(this, AboutActivity.class));
            return(true);
        }
        else if (item.getItemId()==R.id.hiw) {
            startActivity(new Intent(this, HowWorksActivity.class));
            return(true);
        }
        return (super.onOptionsItemSelected(item));
    }
}