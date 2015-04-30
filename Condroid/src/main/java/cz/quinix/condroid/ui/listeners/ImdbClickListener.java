package cz.quinix.condroid.ui.listeners;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import cz.quinix.condroid.R;
import cz.quinix.condroid.model.Annotation;

/**
 * Created by Jan on 14. 6. 2014.
 */
public class ImdbClickListener implements View.OnClickListener {

	private final Activity activity;

	private final Annotation annotation;

	private static boolean warningShown = false;

	public ImdbClickListener(Activity activity, Annotation annotation) {

		this.activity = activity;
		this.annotation = annotation;
	}

	@Override
	public void onClick(View v) {
		isImdbInstalled();

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.imdb.com/title/" + annotation.getImdb() + "/"));
		activity.startActivity(intent);

	}

	private boolean isImdbInstalled() {
		PackageManager pm = activity.getPackageManager();

		try {
			if (pm != null) {
				pm.getPackageInfo("com.imdb.mobile", PackageManager.GET_ACTIVITIES);
				return true;
			}
		} catch (PackageManager.NameNotFoundException e) {
			if (!warningShown) {
				Toast.makeText(activity, R.string.imdbNotInstalled, Toast.LENGTH_LONG).show();
				warningShown = true;
			}
		}
		return false;
	}
}
