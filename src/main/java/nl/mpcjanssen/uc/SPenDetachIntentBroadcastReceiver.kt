package nl.mpcjanssen.uc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SPenDetachIntentBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, penInsertIntent: Intent) {
        if (!penInsertIntent.getBooleanExtra("penInsert", true)) {
            try {
                val launchIntent = Intent(context, UbiquitousCaptureActivity::class.java)
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(launchIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}