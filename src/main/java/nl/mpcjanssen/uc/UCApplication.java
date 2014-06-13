package nl.mpcjanssen.uc;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class UCApplication extends Application {

    private static final int NOTIFY_ID = 0;

    @Override
    public void onCreate() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle("Ubiquitous capture")
                        .setContentText("Capture")
                        .setOngoing(true)
                        .setPriority(Notification.PRIORITY_MIN)
                        ;
        Intent notifyIntent =
                new Intent("nl.mpcjanssen.uc.CAPTURE");
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS );
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
}
