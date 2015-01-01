package nl.mpcjanssen.uc;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;

public class UCApplication extends Application {

    private static final int NOTIFY_ID = 0;
    public CustomPath mPath = new CustomPath();

    @Override
    public void onCreate() {
        int prio = NotificationCompat.PRIORITY_MIN;
        if (useHighPrioNotification()) {
            prio = NotificationCompat.PRIORITY_MAX;
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle("Ubiquitous capture")
                        .setContentText("Capture")
                        .setOngoing(true)
                        .setWhen(0)
                        .setPriority(prio);

        Intent notifyIntent =
                new Intent("nl.mpcjanssen.uc.CAPTURE");
        PendingIntent notifyPIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        mBuilder.setContentIntent(notifyPIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
        Log.v("xxx", "Started");
    }

    public File getCaptureFolder () {
        File folder;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultPath = new File(Environment.getExternalStorageDirectory() , getString(R.string.external_dir)).getPath();
        String path =  sharedPref.getString("pref_capture_folder", defaultPath);
        folder = new File(path);
        return folder;
    }

    public boolean useHighPrioNotification () {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getBoolean("pref_notify_prio_high", false);
    }


    public void setCaptureFolder (File folder) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit().putString("pref_capture_folder", folder.getPath()).apply();
    }
}
