package cz.quinix.condroid.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Reminder;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 11.5.12
 * Time: 22:46
 * To change this template use File | Settings | File Templates.
 */
public class ReminderManager {

    public static void updateAlarmManager(Context context) {
        Log.d("Condroid", "Setting up alarm service");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        DataProvider dp = DataProvider.getInstance(context);

        //find closest event
        Reminder closest = dp.getNextReminder();
        if (closest != null) {
            if (closest.annotation == null) {
                Toast.makeText(context, "Systémová chyba, vymažte data programu.", Toast.LENGTH_LONG).show();
                return;
            }
            if (closest.annotation.getStart() == null) {
                dp.removeReminder(closest.annotation.getPid());
                return;
            }

            PendingIntent pi = PendingIntent.getService(context, 0, new Intent(context, ReminderTask.class), 0);
            long time = closest.annotation.getStart().getTime() - (closest.reminder * 60 * 1000);

            am.set(AlarmManager.RTC_WAKEUP, time, pi);
            Log.d("Condroid", "Alarm will run in " + new Date(time));
        } else {
            Log.d("Condroid", "No next alarm");
        }
    }
}
