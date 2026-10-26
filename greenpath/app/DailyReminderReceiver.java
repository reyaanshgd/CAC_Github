package org.greenpath.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DailyReminderReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("greenpath", Context.MODE_PRIVATE);
        if (!prefs.getBoolean("habit_reminder_enabled", false)) return;

        Set<String> selected = prefs.getStringSet("pledged_habits", new HashSet<>());
        List<Habit> choices = new ArrayList<>();
        if (selected != null && !selected.isEmpty()) {
            for (String id : selected) {
                Habit h = HabitRepository.byId(id);
                if (h != null) choices.add(h);
            }
        }
        if (choices.isEmpty()) choices.addAll(HabitRepository.habits());
        if (choices.isEmpty()) return;

        int index = Math.abs(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) % choices.size();
        Habit habit = choices.get(index);

        Intent open = new Intent(context, MainActivity.class);
        open.putExtra("open_tab", "Pledge");
        PendingIntent content = PendingIntent.getActivity(context, 0, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder b = new Notification.Builder(context)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Today's GreenPath habit")
                .setContentText(habit.title + " · +" + habit.points + " GreenPoints")
                .setStyle(new Notification.BigTextStyle().bigText(habit.title + " — " + habit.description))
                .setAutoCancel(true)
                .setContentIntent(content);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            b.setChannelId(ReminderScheduler.CHANNEL_ID);
        }

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(2201, b.build());
    }
}
