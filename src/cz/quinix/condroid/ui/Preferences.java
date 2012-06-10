package cz.quinix.condroid.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import cz.quinix.condroid.R;
import cz.quinix.condroid.service.UpdatesService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 11.5.12
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */
public class Preferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ListPreference lp = (ListPreference) findPreference("update_check_period");
        CharSequence entry = lp.getEntry();
        if (entry == null) {
            lp.setValueIndex(2);
            entry = lp.getEntry();
        }
        lp.setSummary(entry);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp.registerOnSharedPreferenceChangeListener(this);
        displayLastRun();
    }

    private void displayLastRun() {
        Preference lastUpdate = findPreference("last_update");
        long time = sp.getLong("last_update", 0);;
        if(time == 0) {
            lastUpdate.setSummary("zatím neproběhla");
        }
        else {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            lastUpdate.setSummary(formatter.format(new Date(time)));
        }
    }

    public static void planUpdateService(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!(preferences.getBoolean("update_check", false) && !preferences.getBoolean("updates_found", false))) {
            stopUpdateService(context);
            return;
        }
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        long interval;
        interval = Integer.parseInt(preferences.getString("update_check_period", "60"));
        interval *= 60 * 1000;

        long time = interval + preferences.getLong("last_update", System.currentTimeMillis());

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, new Intent(context, UpdatesService.class), 0);

        am.setInexactRepeating(AlarmManager.RTC, time, interval, pendingIntent);
        Log.d("Condroid", "Update service planned. It will start at " + (new Date(time)));
    }

    public static void stopUpdateService(Context context) {
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, new Intent(context, UpdatesService.class), 0);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("update_check") || s.equals("update_check_period")) {
            if (sp.getBoolean("update_check", false)) {
                planUpdateService(this);
            } else {
                stopUpdateService(this);
            }


        }
        if (s.equals("update_check_period")) {
            ListPreference lp = (ListPreference) findPreference("update_check_period");
            lp.setSummary(lp.getEntry());
        }
    }
}
