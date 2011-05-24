package cz.quinix.condroid.annotations;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.util.Xml;

public class XMLLoader extends AsyncTask<String, Integer, List<Annotation>> {

	@Override
	protected List<Annotation> doInBackground(String... source) {
		List<Annotation> messages = null;
		XmlPullParser pull = Xml.newPullParser();
		Annotation annotation = null;

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
					messages = new ArrayList<Annotation>();
					break;

				case XmlPullParser.START_TAG:
					String name = pull.getName();
					if (name.equalsIgnoreCase("programme")) {
						annotation = new Annotation();
					} else {
						if (name.equalsIgnoreCase("pid")) {
							annotation.pid = pull.nextText();
						}
						if (name.equalsIgnoreCase("talker")) {
							annotation.talker = pull.nextText().trim();
						}
						if (name.equalsIgnoreCase("title")) {
							annotation.title = pull.nextText();
						}
						if (name.equalsIgnoreCase("length")) {
							annotation.length = pull.nextText();
						}
						if (name.equalsIgnoreCase("type")) {
							annotation.type = pull.nextText();
						}
						if (name.equalsIgnoreCase("program-line")) {
							annotation.programLine = pull.nextText();
						}
						if (name.equalsIgnoreCase("annotation")) {
							annotation.annotation = pull.nextText();
						}
					}
					break;

				case XmlPullParser.END_TAG:
					name = pull.getName();
					if (name.equalsIgnoreCase("programme") && annotation != null) {
						messages.add(annotation);
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