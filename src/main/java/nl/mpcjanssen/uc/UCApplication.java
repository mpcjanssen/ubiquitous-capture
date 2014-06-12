package nl.mpcjanssen.uc;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
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
        Intent resultIntent = new Intent(this, UbiquitousCaptureActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack
        stackBuilder.addParentStack(UbiquitousCaptureActivity.class);
// Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
// Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
        Log.v("xxx", "Started");
    }
}
