package cz.quinix.condroid.abstracts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import cz.quinix.condroid.AboutActivity;
import cz.quinix.condroid.R;
import cz.quinix.condroid.annotations.Annotation;
import cz.quinix.condroid.database.DataProvider;

public abstract class CondroidListActivity extends ListActivity {

	protected DataProvider provider;
	
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
	private static DateFormat df = new SimpleDateFormat("HH:mm");
	public View inflanteAnnotation(View v, Annotation annotation) {
	
		TextView tw = (TextView) v.findViewById(R.id.alTitle);
		if (tw != null) {
			tw.setText(annotation.getTitle());
		}
		TextView tw3 = (TextView) v.findViewById(R.id.alThirdLine);
		if (tw != null) {
			tw3.setText(provider.getProgramLine(annotation.getLid()).getName());
		}
		TextView tw2 = (TextView) v.findViewById(R.id.alSecondLine);
		if (tw2 != null) {
			String date = "";
			if(annotation.getStartTime() != null && annotation.getEndTime() != null) {
				date = df.format(annotation.getStartTime()) + " - "+ df.format(annotation.getEndTime())+", "; 
			}
			
			tw2.setText(date + annotation.getAuthor());
		}
		return v;
	}
}
