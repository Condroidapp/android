package cz.quinix.condroid;

import cz.quinix.condroid.conventions.ConventionsActivity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public abstract class CondroidActivity extends ListActivity {

	public static final String PREF_NAME = "condroid";
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflanter = this.getMenuInflater();
		inflanter.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.conSelection:
			SharedPreferences settings = this.getSharedPreferences(PREF_NAME, 0);
			settings.edit().remove("selectedCon").commit();
			Intent intent = new Intent(this, ConventionsActivity.class);
			this.startActivity(intent);
			return true;
		case R.id.about:
			Intent intent2 = new Intent(this, AboutActivity.class);
			this.startActivity(intent2);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}