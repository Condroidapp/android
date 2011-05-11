package cz.quinix.condroid.conventions;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import cz.quinix.condroid.R;
import cz.quinix.condroid.annotations.Convention;

public class ConventionsActivity extends ListActivity {
	/** Called when the activity is first created. */
	private static String list_url = "http://condroid.quinix.cz/api/con-list";
	static final String[] COUNTRIES = new String[] { "Afghanistan", "Albania" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<Convention> l = this.loadCons();
		String[] cons = new String[l.size()];
		Convention c = null; 
		for(int i=0; i<l.size(); i++) {
			c = l.get(i);
			cons[i] = c.name;
		}
		this.setListAdapter(new ArrayAdapter<String>(this, R.layout.cons_list,
				cons));

	}

	private List<Convention> loadCons() {
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
				Toast.makeText(this, "XML error - 1.",
						Toast.LENGTH_LONG).show();
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
				Toast.makeText(this, "XML error - 2.",
						Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			

		

		return messages;
	}

	
}