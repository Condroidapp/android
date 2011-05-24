package cz.quinix.condroid.conventions;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.util.Xml;

public class XMLLoader extends AsyncTask<String, Integer, List<Convention>> {
	
	@Override
	protected List<Convention> doInBackground(String... source) {
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

		return messages;
	}

}
