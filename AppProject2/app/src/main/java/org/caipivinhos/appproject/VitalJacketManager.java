package org.caipivinhos.appproject;


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
    BioLib lib;
    ArrayList<Integer> rrValues;
    int nQRS = 5;
    static boolean isConnected = false;
    private static final String TAG = "VitalJacketManager";

    public void setMacAddress(String value) {
        macAddress = value;
    }

    public void connectToVJ(Context context) throws Exception {
        lib = new BioLib(context, mHandler);
        isConnected = true;
    }

    private void startAcquisition() {
        try{
            Looper.prepare();
            lib.Connect(macAddress,nQRS);
            Looper.loop();
        } catch (Exception e) {
            Log.d(TAG, "VitalJacket: Error Connecting to VitalJacket");
        }
    }

    private void stopAcquisition() throws Exception {
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

    public double instantSession() throws Exception {
        rrValues = new ArrayList<>();
        double instantValue;
        startAcquisition();
        Log.d(TAG, "instantSession: Started Acquisition");
        while(rrValues.size() < 20);
        stopAcquisition();
        Log.d(TAG, "instantSession: Stopped Acquisition");
        instantValue = HRVMethods.rmssdCalculation(rrValues);
        return instantValue;
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == BioLib.MESSAGE_PEAK_DETECTION) {
                BioLib.QRS qrs = (BioLib.QRS) msg.obj;
                Log.d(TAG, "VitalJacket: received rr value "+ qrs.rr);
                rrValues.add(qrs.rr);
            }
        }
    };
}
