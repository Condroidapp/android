package cz.quinix.condroid.conventions;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;
import cz.quinix.condroid.R;

public class ConventionsActivity extends ListActivity {
	/** Called when the activity is first created. */
	private static String list_url = "http://condroid.quinix.cz/api/con-list";
	
	private static Convention[] cons;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		this.setListAdapter(new ConventionListAdapter(this, R.layout.cons_list, this.loadCons()));

	}

	private Convention[] loadCons() {
		if(ConventionsActivity.cons != null) {
			return ConventionsActivity.cons;
		}
		List<Convention> messages = null;
		XmlPullParser pull = Xml.newPullParser();
		Convention con = null;

		try {
			URL url = new URL(ConventionsActivity.list_url);
			URLConnection conn = url.openConnection();

			pull.setInput(conn.getInputStream(), null);
		} catch (Exception ex) {
			Toast.makeText(this, "Can't load conventions list.",
					Toast.LENGTH_LONG).show();

		}
		int eventType = 0;
		try {
			eventType = pull.getEventType();
		} catch (XmlPullParserException e) {
			Toast.makeText(this, "XML error - 1.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
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
			Toast.makeText(this, "XML error - 2.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		Convention[] c = new Convention[messages.size()];
		for (int i = 0; i < messages.size(); i++) {
			c[i] = messages.get(i);
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

	}
}