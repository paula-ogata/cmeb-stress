package org.caipivinhos.appproject;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import Bio.Library.namespace.BioLib;

public class VitalJacketManager {
    static BluetoothAdapter btAdapter;
    private static BluetoothDevice deviceToConnect;
    static private String macAddress;
    static BioLib lib;
    static ArrayList<Integer> rrValues;
    static int nQRS = 5;
    static boolean isConnected = false;
    private static final String TAG = "VitalJacketManager";
    static Context context;
    Handler VJMHandler = new Handler();

    public static void setMacAddress(String value) {
        macAddress = value;
        Log.d(TAG, "setMacAddress: " + macAddress);
    }

    /*public static void connectToVJ(Context context) throws Exception {
        Looper.prepare();
        lib = new BioLib(context, mHandler);
        //deviceToConnect = btAdapter.getRemoteDevice(macAddress);
        lib.Connect(macAddress ,nQRS);
        Looper.loop();
        isConnected = true;
    }*/

    private void startAcquisition() {
        try{
            Looper.prepare();
            lib.Connect(macAddress,nQRS);
            Looper.loop();
        } catch (Exception e) {
            Log.d(TAG, "VitalJacket: Error Connecting to VitalJacket");
        }
    }

    private static void stopAcquisition() throws Exception {
        lib.Disconnect();
    }

    public boolean longSession(Context context) {
        rrValues = new ArrayList<>();
        startAcquisition();
        new CountDownTimer(300000, 1000) {
            public void onTick(long millisUntilFinished) {
                // TO DO If necessary
            }

            public void onFinish() {
                try {
                    stopAcquisition();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        // CALCULOS COM O RRVALUES .....................
        // int[] rrVector = rrValues.toArray;
        // HRVMethods.rmssdCalculation(rrVector) // Calcula RMSSD para o intervalo definido (10s ou 5min, etc)
        // HRVMethods.sdann

        // CALCULO AVG

        // int avg = HRVMethods.avgCalculation(rrVector)
        int stressLevel = 1;
        Date time = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(time);
        String hourBegin = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) +":"+String.valueOf(Calendar.MINUTE);
        Double duration = 1.0;
        String date = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(Calendar.MONTH));
        //DatabaseManager db = new DatabaseManager(context);

        //return db.AddSession(stressLevel, hourBegin, duration, date);
        return true;
    }

    public static double instantSession(Context c) throws Exception {
        rrValues = new ArrayList<>();
        double instantValue;

        context = c;
        ConnectVJ runnable = new ConnectVJ();
        Thread th = new Thread(runnable);
        th.start();
        Log.d(TAG, "instantSession: Started Acquisition");
        while(rrValues.size() < 20){
            Log.d(TAG, "handleMessage: rrValues " + rrValues.size());
        }
        try {
            stopAcquisition();
        }
        catch (Exception e) {
            Log.d(TAG, "onFinish: " + e.getMessage());
        }
        Log.d(TAG, "instantSession: Stopped Acquisition");
        instantValue = HRVMethods.rmssdCalculation(rrValues);
        th.stop();
        return instantValue;
    }

    private static class ConnectVJ implements Runnable {
        @Override
        public void run() {
            Looper.prepare();
            try {
                lib = new BioLib(context, mHandler);
            } catch (Exception e) {
                Log.d(TAG, "VitalJacket: Error Creating VitalJacket");
            }
            try {
                lib.Connect(macAddress ,nQRS);
            } catch (Exception e) {
                Log.d(TAG, "VitalJacket: Error Connecting to VitalJacket");
            }
            Looper.loop();
        }

        static Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == BioLib.MESSAGE_PEAK_DETECTION) {
                    BioLib.QRS qrs = (BioLib.QRS)msg.obj;
                    Log.d(TAG, "VitalJacket: received rr value "+ qrs.rr);
                    rrValues.add(qrs.rr);
                    Log.d(TAG, "handleMessage: rrValues " + rrValues.size());
                }

                if(msg.what == BioLib.MESSAGE_ECG_STREAM) {
                    try{
                        byte[][] ecg = (byte[][]) msg.obj;
                        int nLeads = ecg.length;
                        int nBytes = ecg[0].length;
                        Log.d(TAG, "handleMessage: " + "ECG stream: OK   nBytes: " + nBytes + "   nLeads: " + nLeads);
                    }
                    catch (Exception ex){
                        Log.d(TAG, "handleMessage: Error in ECG");
                    }
                }
            }
        };
    }


}
