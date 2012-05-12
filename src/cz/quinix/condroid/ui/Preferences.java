package cz.quinix.condroid.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import cz.quinix.condroid.R;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 11.5.12
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */
public class Preferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        addPreferencesFromResource(R.xml.preference);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);


        Preference lastUpdate = findPreference("last_update");
        lastUpdate.setSummary(sp.getString("last_update","zatím neproběhla"));
    }
}
