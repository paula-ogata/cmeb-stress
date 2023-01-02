package org.caipivinhos.appproject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyBackgroundService extends Service {
    Thread th;
    boolean acquisition = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DatabaseManager db = new DatabaseManager(this);
        double mediumLevel = db.getMediumLevel();

        Toast.makeText(this, "Background Acquisition Started", Toast.LENGTH_LONG).show();


        // Thread that gives the terminal a message, every 2 seconds (just to see if its working in background)
        th = new Thread(
                () -> {
                    while (acquisition) {
                        VitalJacketManager.longSession(this, mediumLevel);
                    }
                }
        );
        th.start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop LongSession command
        acquisition = false;
        th.interrupt();

        Toast.makeText(this, "Background Acquisition Stopped", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
