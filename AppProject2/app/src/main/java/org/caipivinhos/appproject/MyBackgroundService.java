package org.caipivinhos.appproject;

import static android.content.ContentValues.TAG;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyBackgroundService extends Service {
    Thread th;
    boolean acquisition = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DatabaseManager db = null;
        double mediumLevel;
        db = new DatabaseManager(this);
        mediumLevel = db.getMediumLevel();
        Context context;
        context = this;


        try {
            Toast.makeText(this, "LongSession Running", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            Log.d(TAG, "LongAcquisition: Error");
        }

        // Thread that gives the terminal a message, every 2 seconds (just to see if its working in background)
        th = new Thread(
                () -> {
                    while (acquisition) {

                        double valueR = 0;
                        valueR = VitalJacketManager.longSession(context, mediumLevel);
                        double finalValueR = valueR;

                        Log.e("Service", "The Service Background is running");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

        );
        th.start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop LongSession comand
        acquisition = false;
        th.interrupt();

        Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show();
    }

    public void stopLongAcquisition(){
        th.interrupt();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
