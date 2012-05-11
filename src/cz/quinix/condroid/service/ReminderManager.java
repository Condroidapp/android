package cz.quinix.condroid.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.util.Log;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Reminder;
import cz.quinix.condroid.ui.ShowAnnotation;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 11.5.12
 * Time: 22:46
 * To change this template use File | Settings | File Templates.
 */
public class ReminderManager {

    public static void updateAlarmManager(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        DataProvider dp = DataProvider.getInstance(context);

        //find closest event
        Reminder closest = dp.getNextReminder();
        if(closest != null) {

            PendingIntent pi = PendingIntent.getService(context,0,new Intent(context, ReminderTask.class),0);
            long time = /*annotation.getStartTime().getTime()*/ System.currentTimeMillis() + closest.reminder*10*1000;

            am.set(AlarmManager.RTC_WAKEUP, time, pi);
        }
    }
}
