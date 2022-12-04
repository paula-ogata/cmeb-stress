package org.caipivinhos.appproject;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;

import Bio.Library.namespace.BioLib;

public class VitalJacketManager {
    private String macAddress;
    BioLib lib;
    ArrayList<Integer> rrValues;

    public boolean connectToVJ(Context context, String macAddress) throws Exception {
        this.macAddress = macAddress;
        try {
            lib = new BioLib(context, mHandler);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean startAcquisition(int nQRS) {
        try{
            Looper.prepare();
            lib.Connect(macAddress,nQRS);
            Looper.loop();
        } catch (Exception e) {
            return false;
        }
        return true;
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
}
