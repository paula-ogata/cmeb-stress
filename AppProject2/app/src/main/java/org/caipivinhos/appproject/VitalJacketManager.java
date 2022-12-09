package org.caipivinhos.appproject;


import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import Bio.Library.namespace.BioLib;

public class VitalJacketManager implements Runnable {
    static private String macAddress;
    BioLib lib;
    ArrayList<Integer> rrValues;
    int nQRS = 5;
    static boolean isConnected = false;
    boolean isFinished = false;
    double instantValue;

    public void setMacAddress(String value) {
        macAddress = value;
    }
    public boolean connectToVJ(Context context) throws Exception {
        try {
            lib = new BioLib(context, mHandler);
        } catch (Exception e){
            return false;
        }
        isConnected = true;
        return true;
    }

    private void startAcquisition() {
        try{
            Looper.prepare();
            lib.Connect(macAddress,nQRS);
            Looper.loop();
        } catch (Exception e) {
            // TO DO
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

    public void instantSession() throws Exception {
        rrValues = new ArrayList<>();
        startAcquisition();
        isFinished = false;
        /*
        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                // TO DO If necessary
            }

            public void onFinish() {
                try {
                    stopAcquisition();
                    instantValue = HRVMethods.rmssdCalculation(rrValues);
                } catch (Exception e) {
                    e.printStackTrace();
                    instantValue = 5.0;
                }

                isFinished = true;
            }
        }.start(); */


        while(rrValues.size() < 20);
        stopAcquisition();
        instantValue = HRVMethods.rmssdCalculation(rrValues);
    }

    public double getInstantValue() {
        return instantValue;
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == BioLib.MESSAGE_PEAK_DETECTION) {
                BioLib.QRS qrs = (BioLib.QRS) msg.obj;
                rrValues.add(qrs.rr);
            }
        }
    };

    @Override
    public void run() {
        instantValue = 5.0;
        //instantSession();
    }

}
