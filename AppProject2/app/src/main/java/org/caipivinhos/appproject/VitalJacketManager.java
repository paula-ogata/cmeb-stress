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
    static private String macAddress;
    static BioLib lib;
    static ArrayList<Integer> rrValues;
    static int nQRS = 5;
    private static final String TAG = "VitalJacketManager";
    static Context context;

    public static void setMacAddress(String value) {
        macAddress = value;
        Log.d(TAG, "setMacAddress: " + macAddress);
    }

    public static double longSession(Context c, double mediumLevel) {

        if(macAddress==null) {
            return -1;
        }

        rrValues = new ArrayList<>();

        context = c;
        ConnectVJ runnable = new ConnectVJ();
        Thread th = new Thread(runnable);
        th.start();
        Log.d(TAG, "longSession: Started Acquisition");
        while(rrValues.size() < 300) {
            Log.d(TAG, "longSession: rrValues size" + rrValues.size());
        }

        try{
            lib.Disconnect();
            Log.d(TAG, "longSession: Stopped Acquisition");
        }
        catch (Exception e) {
            Log.d(TAG, "longSession: Error Stopping " + e.getMessage());
        }

        // CALCULOS COM O RRVALUES .....................

        DatabaseManager db = new DatabaseManager(context);
        double rrAvg = HRVMethods.rmssdCalculation(rrValues);
        int stressPercentage = HRVMethods.getStressPercentage(rrValues, mediumLevel);

        Date time = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(time);
        String hourBegin = calendar.get(Calendar.HOUR_OF_DAY) +":"+ Calendar.MINUTE;
        String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR);

        db.AddSession(rrAvg, stressPercentage, hourBegin, date);
        return 0;
    }

    public static double instantSession(Context c, double mediumLevel) throws Exception {

        if(macAddress==null) {
            return -1;
        }

        rrValues = new ArrayList<>();
        double instantValue;
        context = c;
        ConnectVJ runnable = new ConnectVJ();
        Thread th = new Thread(runnable);
        th.start();
        Log.d(TAG, "instantSession: Started Acquisition");
        while(rrValues.size() < 20){
            Log.d(TAG, "instantSession: rrValues size" + rrValues.size());
        }
        try {
            lib.Disconnect();
            Log.d(TAG, "instantSession: Stopped Acquisition");
        }
        catch (Exception e) {
            Log.d(TAG, "instantSession: Error Stopping " + e.getMessage());
        }

        instantValue = HRVMethods.getStressPercentage(rrValues, mediumLevel);
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
