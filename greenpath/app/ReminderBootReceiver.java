package org.greenpath.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ReminderBootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("greenpath", Context.MODE_PRIVATE);
        if (prefs.getBoolean("habit_reminder_enabled", false)) ReminderScheduler.schedule(context);
    }
}
