package cz.quinix.condroid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import cz.quinix.condroid.annotations.Annotation;
import cz.quinix.condroid.database.DataProvider;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RunningActivity extends ListActivity {

	List<Annotation> list = null;
	DataProvider provider = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		provider = DataProvider.getInstance(getApplicationContext());
		list = provider.getRunningAndNext();
		this.setListAdapter(new CategoryAdapter(list));
	}

	class CategoryAdapter extends ArrayAdapter<Annotation> {

		private List<Annotation> map;
		private String previous = "";
		private int correction = 0;
		private DateFormat db = new SimpleDateFormat("yyyy-MM-dd kk:mm");
		private DateFormat read = new SimpleDateFormat("EEEE dd.MM. kk:mm",
				new Locale("cs", "CZ"));

		public CategoryAdapter(List<Annotation> map) {
			super(RunningActivity.this, android.R.layout.simple_list_item_1,
					map);
			this.map = map;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Annotation it = this.getItem(position);

			View v = convertView;
			//if (v == null) {
				if (it.getTitle() == "break") {
					LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = vi.inflate(R.layout.running_simple, null);
				} else {
					LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					v = vi.inflate(R.layout.annotation_list_item, null);
				}
			//}
			if (it != null) {
				if (it.getTitle() == "break") {
					Log.d("S" + position, db.format(it.getStartTime()));

					TextView tw = (TextView) v.findViewById(R.id.tRunningTitle);
					if (it.getStartTime().compareTo(new Date()) <= 0) {
						tw.setText("Právě beží");
					} else {
						tw.setText(read.format(it.getStartTime()));
					}

					return v;
				}

				TextView tw = (TextView) v
						.findViewById(R.id.annotation_list_title);
				if (tw != null) {
					tw.setText(it.getTitle());
				}
				TextView tw3 = (TextView) v
						.findViewById(R.id.annotation_list_info);
				if (tw != null) {
					tw3.setText(provider.getProgramLine(it.getLid()).getName());
				}
				TextView tw2 = (TextView) v
						.findViewById(R.id.annotation_list_author);
				if (tw2 != null) {
					tw2.setText(it.getPid() + ", " + it.getAuthor());
				}
				return v;
			}

			return super.getView(position, convertView, parent);
		}
	}
}
