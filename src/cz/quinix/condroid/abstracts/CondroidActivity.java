package cz.quinix.condroid.abstracts;


import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import cz.quinix.condroid.AboutActivity;
import cz.quinix.condroid.R;

public abstract class CondroidActivity extends Activity {

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
		case R.id.about:
			Intent intent2 = new Intent(this, AboutActivity.class);
			this.startActivity(intent2);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}