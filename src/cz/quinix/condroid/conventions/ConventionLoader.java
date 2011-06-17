package cz.quinix.condroid.conventions;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import cz.quinix.condroid.AsyncTaskListener;
import cz.quinix.condroid.CondroidActivity;
import cz.quinix.condroid.CondroidXMLTask;
import cz.quinix.condroid.XMLProccessException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;

public class ConventionLoader extends AsyncTask<Void, Void, List<Convention>> {
	private static final String list_url = "http://condroid.fan-project.com/api/con-list";
	
	private AsyncTaskListener listener;
	
	public ConventionLoader(AsyncTaskListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected void onPostExecute(List<Convention> result) {
		this.listener.onAsyncTaskCompleted(result);
	}

	@Override
	protected List<Convention> doInBackground(Void... source) {
		List<Convention> messages = new ArrayList<Convention>();
		XmlPullParser pull = Xml.newPullParser();
		Convention con = null;
		try {
		try {
			URL url = new URL(list_url);
			URLConnection conn = url.openConnection();

			pull.setInput(conn.getInputStream(), null);
		} catch (Exception ex) {
			throw new XMLProccessException("Stažení seznamu akcí se nezdařilo.", ex);
		}
		int eventType = 0;
		try {
			eventType = pull.getEventType();
		} catch (XmlPullParserException e) {
			throw new XMLProccessException("Zpracování seznamu akcí se nezdařilo", e);
		}

		try {
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;

				case XmlPullParser.START_TAG:
					String name = pull.getName();
					if (name.equalsIgnoreCase("convention")) {
						con = new Convention();
					} else {
						if (name.equalsIgnoreCase("name")) {
							con.setName(pull.nextText());
						}
						if (name.equalsIgnoreCase("icon")) {
							con.setIconUrl(pull.nextText());
						}
						if (name.equalsIgnoreCase("date")) {
							con.setDate(pull.nextText());
						}
						if (name.equalsIgnoreCase("cid")) {
							con.setCid(Integer.parseInt(pull.nextText()));
						}
						if (name.equalsIgnoreCase("data-url")) {
							con.setDataUrl(pull.nextText());
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
			Log.w(CondroidActivity.PREF_NAME, "XML error", e);
			throw new XMLProccessException("Zpracování zdroje se nezdařilo.", e);
		}
		} catch (XMLProccessException ex) {
			//this.message = ex.getMessage();
		}
		return messages;
	}
	
	
}