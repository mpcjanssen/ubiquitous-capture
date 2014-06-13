package nl.mpcjanssen.uc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(this.getClass().getName(), "Started after boot");
    }
}
