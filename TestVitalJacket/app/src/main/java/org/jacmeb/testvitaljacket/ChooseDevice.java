package org.jacmeb.testvitaljacket;
//...
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

public class ChooseDevice extends AppCompatActivity {

    public static String SELECT_DEVICE_ADDRESS = "device_address";
    public static final int CHANGE_MACADDRESS = 100;
    private ArrayAdapter<String> listAdapter;
    private String selectedValue = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);

        Button buttonOK = (Button) findViewById(R.id.cmdOK);
        buttonOK.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(SELECT_DEVICE_ADDRESS, selectedValue);
            startActivity(intent);
        });

        try {
            ListView mainListView = (ListView) findViewById(R.id.lstDevices);

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

            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick( AdapterView<?> parent, View item, int position, long id)
                {
                    selectedValue = (String) listAdapter.getItem(position);

                    String[] aux = selectedValue.split("   ");
                    selectedValue = aux[0];
                }
            });
        }
        catch (SecurityException exception)
        {
            buttonOK.setText("N/A");
        }

    }
}