package cz.quinix.condroid.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.inject.Inject;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Reminder;
import cz.quinix.condroid.ui.activities.ShowAnnotation;
import roboguice.service.RoboService;

import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 11.5.12
 * Time: 23:21
 * To change this template use File | Settings | File Templates.
 */
public class ReminderTask extends RoboService {

    @Inject DataProvider provider;


    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handleCommand(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return Service.START_STICKY;
    }

    public static SimpleDateFormat df = new SimpleDateFormat("HH:mm");

    private void handleCommand(Intent intent) {
        Reminder r = provider.getNextReminder();

        if (r != null) {
            Annotation annotation = r.annotation;
            NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            Notification n = new Notification(R.drawable.icon_status_bar,
                    annotation.getTitle() + " začíná " + (r.reminder > 0 ? "za " + r.reminder + (r.reminder >= 5 ? " minut!" : " minuty!") : "právě teď!"), System.currentTimeMillis());

            Intent i = new Intent(this, ShowAnnotation.class);
            i.putExtra("annotation", annotation);
            PendingIntent pi = PendingIntent.getActivity(this, annotation.getPid(), i, PendingIntent.FLAG_ONE_SHOT);
            try {
                n.setLatestEventInfo(this, annotation.getTitle(), "Začátek v " + df.format(annotation.getStart()), pi);

                n.flags |= Notification.FLAG_AUTO_CANCEL;

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                if (sp.getBoolean("reminder_tone_check", false)) {
                    String sound = sp.getString("reminder_tone", null);
                    if (sound != null) {
                        n.sound = Uri.parse(sound);
                    }
                }
                if (sp.getBoolean("reminder_vibrate", false)) {
                    n.defaults |= Notification.DEFAULT_VIBRATE;
                }
                if (sp.getBoolean("reminder_flash", true)) {
                    n.flags |= Notification.FLAG_SHOW_LIGHTS;
                    n.ledARGB = 0xff00ff00;
                    n.ledOnMS = 300;
                    n.ledOffMS = 1000;
                }

                nm.notify(annotation.getPid(), n);
            } catch (NullPointerException e) {
                Log.d("Condroid", "Null pointer in service", e);
            } finally {
                if (annotation != null) {
                    provider.removeReminder(annotation.getPid());
                }

            }
        }
        ReminderManager.updateAlarmManager(this);

        this.stopSelf();
    }

}
