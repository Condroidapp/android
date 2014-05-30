package cz.quinix.condroid.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cz.quinix.condroid.ui.Preferences;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 13.5.12
 * Time: 0:07
 * To change this template use File | Settings | File Templates.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Condroid", "BootReciever called");
        ReminderManager.updateAlarmManager(context);
    }
}
