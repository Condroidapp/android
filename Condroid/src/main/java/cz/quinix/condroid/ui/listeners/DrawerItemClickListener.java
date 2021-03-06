package cz.quinix.condroid.ui.listeners;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.View;

import java.util.List;
import java.util.Locale;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.ui.AboutDialog;
import cz.quinix.condroid.ui.Preferences;
import cz.quinix.condroid.ui.ReminderList;
import cz.quinix.condroid.ui.activities.MainActivity;
import cz.quinix.condroid.ui.activities.NeighbourhoodListActivity;
import cz.quinix.condroid.ui.activities.WelcomeActivity;

/**
 * Created by Jan on 1. 6. 2014.
 */
public class DrawerItemClickListener implements View.OnClickListener {

	private MainActivity parentActivity;

	private DataProvider provider;

	public DrawerItemClickListener(MainActivity parentActivity, DataProvider provider) {
		this.parentActivity = parentActivity;
		this.provider = provider;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tdRestaurants:
				Intent in2 = new Intent(parentActivity, NeighbourhoodListActivity.class);
				parentActivity.startActivity(in2);
				break;
			case R.id.tdMap:
				Convention event = provider.getCon();
				if (event.getGps() != null) {

					String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=17", event.getGps().lat, event.getGps().lon);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					PackageManager manager = parentActivity.getPackageManager();
					List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
					if (infos.size() > 0) {
						parentActivity.startActivity(intent);
					}
				}
				break;
			case R.id.tdWeb:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(provider.getCon().getUrl()));
				parentActivity.startActivity(browserIntent);
				break;
			case R.id.tdAnother:
				//load event
				Intent intent = new Intent(parentActivity, WelcomeActivity.class);
				intent.putExtra("force", true);
				parentActivity.startActivity(intent);

				break;
			case R.id.tdReminders:
				Intent in = new Intent(parentActivity, ReminderList.class);
				parentActivity.startActivity(in);
				break;
			case R.id.tdSettings:
				Intent i = new Intent(parentActivity, Preferences.class);
				parentActivity.startActivity(i);
				break;
			case R.id.tdAbout:
				AboutDialog aboutDialog = new AboutDialog(parentActivity);
				parentActivity.setActivityResultListener(aboutDialog);
				aboutDialog.show();

				break;
		}
		this.parentActivity.closeDrawer();
	}
}
