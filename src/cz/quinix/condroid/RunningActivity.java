package cz.quinix.condroid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cz.quinix.condroid.annotations.Annotation;
import cz.quinix.condroid.annotations.ShowAnnotation;
import cz.quinix.condroid.database.DataProvider;

public class RunningActivity extends CondroidListActivity {

	List<Annotation> list = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		provider = DataProvider.getInstance(getApplicationContext());
		list = provider.getRunningAndNext();
		this.setListAdapter(new CategoryAdapter(list, this));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = this.getMenuInflater();
		i.inflate(R.menu.running, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			this.list.clear();
			this.list.addAll(provider.getRunningAndNext());
			((ArrayAdapter) this.getListAdapter()).notifyDataSetChanged();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Annotation selected = (Annotation) l.getItemAtPosition(position);
		if (selected.getTitle() != "break") {
			
			Intent intent = new Intent(this, ShowAnnotation.class);
			intent.putExtra("annotation", selected);
			this.startActivity(intent);
		}
	}

}

class CategoryAdapter extends ArrayAdapter<Annotation> {

	private DateFormat read = new SimpleDateFormat("EEEE dd.MM. HH:mm",
			new Locale("cs", "CZ"));
	private RunningActivity caller;

	public CategoryAdapter(List<Annotation> map, RunningActivity caller) {
		super(caller, android.R.layout.simple_list_item_1, map);
		this.caller = caller;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Annotation it = this.getItem(position);

		View v = this.getLayout(it);
		if (it != null) {
			if (it.getTitle() == "break") {
				TextView tw = (TextView) v.findViewById(R.id.tRunningTitle);
				if (it.getAnnotation().equals("now")) {
					tw.setText("Právě běží");
				} else {
					tw.setText(read.format(it.getStartTime()));
				}
				v.setFocusable(false);
				return v;
			}

			return caller.inflanteAnnotation(v, it);

		}

		return super.getView(position, convertView, parent);
	}

	private View getLayout(Annotation it) {
		if (it.getTitle() == "break") {
			LayoutInflater vi = (LayoutInflater) caller
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return vi.inflate(R.layout.running_simple, null);
		} else {
			LayoutInflater vi = (LayoutInflater) caller
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return vi.inflate(R.layout.annotation_list_item, null);
		}
	}
}
