package cz.quinix.condroid.loader;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.widget.Toast;
import cz.quinix.condroid.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import cz.quinix.condroid.XMLProccessException;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.model.Convention;

import android.util.Log;
import android.util.Xml;

public class ConventionLoader extends ListenedAsyncTask<Void, Void> {
	
	public ConventionLoader(AsyncTaskListener listener) {
		super(listener);
	}


	private static final String list_url = "http://condroid.fan-project.com/api/con-list";

    @Override
    protected void showDialog() {
        if(parentActivity != null)
            pd= ProgressDialog.show(parentActivity, "", parentActivity.getString(R.string.loading), true);
    }

    @Override
	protected List<Convention> doInBackground(Void... source) {
		List<Convention> messages = new ArrayList<Convention>();
		XmlPullParser pull = Xml.newPullParser();
		Convention con = null;

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
						if (name.equalsIgnoreCase("message")) {
							con.setMessage(pull.nextText());
						}
						if (name.equalsIgnoreCase("locations-file")) {
							con.setLocationsFile(pull.nextText());
						}
						if (name.equalsIgnoreCase("provides-timetable")) {
							if(pull.nextText()=="yes") {
								con.setHasTimetable(true);
							}
						}
						if (name.equalsIgnoreCase("provides-annotations")) {
							if(pull.nextText()=="yes") {
								con.setHasAnnotations(true);
							}
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
			Log.e("Condroid","Exception when parsing con list XML", e);
//            Toast.makeText(parentActivity,"Data download failed - "+e.getMessage(),Toast.LENGTH_LONG).show();
			//throw new XMLProccessException("Zpracování zdroje se nezdařilo.", e);
		}
		return messages;
	}
	
	
}
