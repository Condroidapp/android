package cz.quinix.condroid.abstracts;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;

import java.util.Date;

public abstract class CondroidActivity extends Activity {

    public static final String PREF_NAME = "condroid";


    public static String getUniqueDeviceIdentifier(Context context) {
        if(Build.VERSION.SDK_INT < 9) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String ident = "pre-9-"+ Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)+"-"+(new Date().toGMTString());
            if(sp.getString("device_id",ident) == ident) {
                SharedPreferences.Editor e = sp.edit();
                e.putString("device_id", ident);
                e.commit();
            }
            return sp.getString("device_id", ident);
        } else {
            return Build.SERIAL;
        }
    }
}