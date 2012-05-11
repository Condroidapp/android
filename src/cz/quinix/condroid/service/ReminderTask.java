package cz.quinix.condroid.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Reminder;
import cz.quinix.condroid.ui.ShowAnnotation;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 11.5.12
 * Time: 23:21
 * To change this template use File | Settings | File Templates.
 */
public class ReminderTask extends Service {

    private static int notificationId = 1;

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

    public static DateTimeFormatter df = DateTimeFormat.mediumTime();

    private void handleCommand(Intent intent) {
        Reminder r = DataProvider.getInstance(getApplicationContext()).getNextReminder();


        Annotation annotation = r.annotation;
        NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        Notification n = new Notification(R.drawable.icon, annotation.getTitle() + " začíná z "+r.reminder+" minut!",System.currentTimeMillis());

        Intent i = new Intent(this, ShowAnnotation.class);
        i.putExtra("annotation",annotation);
        PendingIntent pi = PendingIntent.getActivity(this,0,i,0);
        n.setLatestEventInfo(this, annotation.getTitle(), "Začátek v "+ df.print(annotation.getStartTime().getTime()),pi);
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        nm.notify(notificationId++,n);
        DataProvider.getInstance(getApplicationContext()).removeReminder(annotation.getPid());

        //plan new
        ReminderManager.updateAlarmManager(this);

        this.stopSelf();

    }

}
