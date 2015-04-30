package cz.quinix.condroid.ui.dataLoading;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;

import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.ui.activities.MainActivity;

public class UpdateChecker {

	private static final String TAG = UpdateChecker.class.getName();

	private MainActivity parent;

	private Convention event;

	public UpdateChecker(MainActivity parent, Convention event) {
		this.parent = parent;
		this.event = event;
	}

	public void execute() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(parent);
		if (!preferences.getBoolean("auto_updates", false)) {
			return;
		}

		int interval = Integer.parseInt(preferences.getString("auto_updates_interval", "60"));

		Date lastCheck = new Date();
		lastCheck.setTime(preferences.getLong("last_update", 946684800000L)); //1.1.2000

		lastCheck.setTime(lastCheck.getTime() + interval * 60 * 1000);

		Date now = new Date();

		if (!now.after(lastCheck)) {
			return;
		}

		ConnectivityManager c = (ConnectivityManager) parent.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (c.getActiveNetworkInfo() == null || !c.getActiveNetworkInfo().isConnected()) {
			Log.d(TAG, "Network not available.");
			return;
		}

		parent.setRefreshActionButtonState(true);
		Downloader downloader = new Downloader(parent, event, true, false);

		downloader.invoke();

		SharedPreferences.Editor editor = preferences.edit();

		editor.putLong("last_update", now.getTime());
		editor.commit();
		now.setTime(now.getTime() + interval * 60 * 1000);
		Log.d(TAG, "Update check launched, next run after " + now.toLocaleString());
	}
}
