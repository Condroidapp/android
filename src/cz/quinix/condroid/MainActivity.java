package cz.quinix.condroid;

import cz.quinix.condroid.conventions.ConventionsActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		

	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflanter = this.getMenuInflater();
		inflanter.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.conSelection:
			Intent intent = new Intent(this, ConventionsActivity.class);
			this.startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}