package org.jacmeb.testvitaljacket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

import Bio.Library.namespace.BioLib;


public class MainActivity extends AppCompatActivity {
    BluetoothAdapter btAdapter;
    String macAddress;  //MUDAR
    BioLib lib;
    private BluetoothDevice deviceToConnect;
    Button btConnect, btDisconnect, rtcBt, getRtcBt, requestBt;
    TextView getRtcText, ecgText, textHR, textDeviceId;

    private byte[][] ecg = null;
    private int nBytes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        macAddress = getIntent().getStringExtra("device_address");
        btConnect = (Button) findViewById(R.id.connectBt);
        btConnect.setOnClickListener(view -> {
            try {
                Looper.prepare();
                lib = new BioLib(this, mHandler);
                deviceToConnect = btAdapter.getRemoteDevice(macAddress);
                lib.Connect(macAddress, 5);
                Looper.loop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btDisconnect = (Button) findViewById(R.id.disconnectBt);
        btDisconnect.setOnClickListener(view -> {
            try
            {
                lib.Disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        rtcBt = (Button) findViewById(R.id.rtcBt);
        rtcBt.setOnClickListener(view -> {
            try
            {
                Date date = new Date();
                lib.SetRTC(date);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });


        getRtcText = (TextView)findViewById(R.id.getRtcText);
        getRtcBt = (Button) findViewById(R.id.getRtcBt);
        getRtcBt.setOnClickListener(view -> {
            try
            {
                if(lib.GetRTC()) {
                    getRtcText.setText("Worked");
                }
                else{
                    getRtcText.setText("Didn't worked");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        requestBt = (Button)findViewById(R.id.requestBt);
        requestBt.setOnClickListener(view -> {
            try {
                lib.Request(macAddress, 30);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ecgText = (TextView) findViewById(R.id.ecgText);
        textHR = (TextView) findViewById(R.id.textHR);
        textDeviceId = (TextView) findViewById(R.id.textDeviceId);
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BioLib.MESSAGE_ECG_STREAM:
                    try{
                        ecg = (byte[][]) msg.obj;
                        int nLeads = ecg.length;
                        nBytes = ecg[0].length;
                        ecgText.setText("ECG stream: OK   nBytes: " + nBytes + "   nLeads: " + nLeads);
                    }
                    catch (Exception ex){
                        ecgText.setText("Error in ECG");
                    }
                    break;

                case BioLib.MESSAGE_PEAK_DETECTION:
                    BioLib.QRS qrs = (BioLib.QRS)msg.obj;
                    textHR.setText("PEAK: " + qrs.position + "  BPMi: " + qrs.bpmi + " bpm  BPM: " + qrs.bpm + " bpm  R-R: " + qrs.rr + " ms");
                    break;

                case BioLib.MESSAGE_DEVICE_ID:
                    String deviceId = (String)msg.obj;
                    textDeviceId.setText("Device Id: " + deviceId);
                    break;
            }
        }
    };
}