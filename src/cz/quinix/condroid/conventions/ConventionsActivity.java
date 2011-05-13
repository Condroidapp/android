package cz.quinix.condroid.conventions;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import cz.quinix.condroid.R;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ConventionsActivity extends ListActivity {
	/** Called when the activity is first created. */
	private static String list_url = "http://condroid.quinix.cz/api/con-list";
	
	private static Convention[] cons;
	private ConventionListAdapter c;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		c = new ConventionListAdapter(this, R.layout.cons_list, this.loadCons());
		this.setListAdapter(c);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflanter = this.getMenuInflater();
		inflanter.inflate(R.menu.conventions_list, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cons_refresh:
			this.cons = null;
			this.c.setItems(this.loadCons()).notifyDataSetChanged();
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private Convention[] loadCons() {
		if(ConventionsActivity.cons != null) {
			return ConventionsActivity.cons;
		}
		
		
		Convention[] c = null;
		try {
			c = new ConventionXMLLoder().execute(ConventionsActivity.list_url).get();
		} catch (Exception ex) {
			Toast.makeText(this, "Can't load conventions list.",
					Toast.LENGTH_LONG).show();

		}
		return c;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}

	private class ConventionListAdapter extends ArrayAdapter<Convention> {

		private Convention[] items;

		public ConventionListAdapter(Context context, int textViewResourceId,
				Convention[] items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.convention_list_item, null);
			}

			Convention it = items[position];
			if (it != null) {
				ImageView iv = (ImageView) v.findViewById(R.id.convention_list_item_image);
				if (iv != null) {
					iv.setImageDrawable(it.getImage());
				}
				TextView tw = (TextView) v.findViewById(R.id.convention_list_item_text);
				if (tw != null) {
					tw.setText(it.name);
				}
				TextView tw2 = (TextView) v.findViewById(R.id.convention_list_item_date);
				if (tw2 != null) {
					tw2.setText(it.date);
				}
			}

			return v;
		}
		
		public ConventionListAdapter setItems(Convention[] c) {
			this.items = c;
			return this;
		}

	}
	private class ConventionXMLLoder extends AsyncTask<String, Integer, Convention[]> {
		ProgressDialog progress;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			this.progress = ProgressDialog.show(ConventionsActivity.this, "", "Načítám.", true);
		}
		
		@Override
		protected void onPostExecute(Convention[] result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			this.progress.cancel();
		}

		@Override
		protected Convention[] doInBackground(String... source) {
			List<Convention> messages = null;
			XmlPullParser pull = Xml.newPullParser();
			Convention con = null;

			try {
				URL url = new URL(source[0]);
				URLConnection conn = url.openConnection();

				pull.setInput(conn.getInputStream(), null);
			} catch (Exception ex) {
				throw new RuntimeException(ex);

			}
			int eventType = 0;
			try {
				eventType = pull.getEventType();
			} catch (XmlPullParserException e) {
				throw new RuntimeException(e);
			}
			try {
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						messages = new ArrayList<Convention>();
						break;

					case XmlPullParser.START_TAG:
						String name = pull.getName();
						if (name.equalsIgnoreCase("convention")) {
							con = new Convention();
						} else {
							if (name.equalsIgnoreCase("name")) {
								con.name = pull.nextText();
							}
							if (name.equalsIgnoreCase("icon")) {
								con.iconUrl = pull.nextText();
							}
							if (name.equalsIgnoreCase("date")) {
								con.date = pull.nextText();
							}
							if (name.equalsIgnoreCase("cid")) {
								con.cid = Integer.parseInt(pull.nextText());
							}
						}
						break;

					case XmlPullParser.END_TAG:
						name = pull.getName();
						if (name.equalsIgnoreCase("convention") && con != null) {
							messages.add(con);
						}
						break;
					default:
						break;
					}
					eventType = pull.next();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			Convention[] c = new Convention[messages.size()];
			for (int i = 0; i < messages.size(); i++) {
				c[i] = messages.get(i);
			}
			return c;
		}

	}
}