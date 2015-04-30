package cz.quinix.condroid.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.quinix.condroid.R;
import cz.quinix.condroid.ui.activities.MainActivity;

public class Preferences extends SherlockPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		addPreferencesFromResource(R.xml.preference);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		ListPreference lp = (ListPreference) findPreference("auto_updates_interval");
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
		long time = sp.getLong("last_update", 0);

		if (time == 0) {
			lastUpdate.setSummary("zatím neproběhla");
		} else {
			SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			lastUpdate.setSummary(formatter.format(new Date(time)));
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		if (s.equals("update_check") || s.equals("update_check_period")) {
			/*if (sp.getBoolean("update_check", false)) {
                planUpdateService(this);
            } else {
                stopUpdateService(this);
            }*/

		}
		if (s.equals("update_check_period")) {
			ListPreference lp = (ListPreference) findPreference("update_check_period");
			lp.setSummary(lp.getEntry());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
