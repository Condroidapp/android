package cz.quinix.condroid.annotations;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import cz.quinix.condroid.CondroidActivity;
import cz.quinix.condroid.CondroidXMLTask;
import cz.quinix.condroid.XMLProccessException;

import android.util.Xml;

public class XMLLoader extends CondroidXMLTask<List<Annotation>> {

	public XMLLoader(CondroidActivity caller) {
		super(caller);
	}

	@Override
	protected List<Annotation> doInBackground(String... source) {
		List<Annotation> messages = new ArrayList<Annotation>();
		XmlPullParser pull = Xml.newPullParser();
		Annotation annotation = null;
		try {
			try {
				URL url = new URL(source[0]);
				URLConnection conn = url.openConnection();

				pull.setInput(conn.getInputStream(), null);
			} catch (Exception ex) {
				throw new XMLProccessException("Stažení seznamu anotací se nezdařilo.", ex);
			}
			int eventType = 0;
			try {
				eventType = pull.getEventType();
			} catch (XmlPullParserException e) {
				throw new XMLProccessException("Zpracování seznamu anotací se nezdařilo", e);
			}
			try {
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;

					case XmlPullParser.START_TAG:
						String name = pull.getName();
						if (name.equalsIgnoreCase("programme")) {
							annotation = new Annotation();
						} else {
							if (name.equalsIgnoreCase("pid")) {
								annotation.setPid(pull.nextText());
							}
							if (name.equalsIgnoreCase("talker")) {
								annotation.setAuthor(pull.nextText());
							}
							if (name.equalsIgnoreCase("title")) {
								annotation.setTitle(pull.nextText());
							}
							if (name.equalsIgnoreCase("length")) {
								annotation.setLength(pull.nextText());
							}
							if (name.equalsIgnoreCase("type")) {
								annotation.setType(pull.nextText());
							}
							if (name.equalsIgnoreCase("program-line")) {
								annotation.setProgramLine(pull.nextText());
							}
							if (name.equalsIgnoreCase("annotation")) {
								annotation.setAnnotation(pull.nextText());
							}
						}
						break;

					case XmlPullParser.END_TAG:
						name = pull.getName();
						if (name.equalsIgnoreCase("programme")
								&& annotation != null) {
							messages.add(annotation);
						}
						break;
					default:
						break;
					}
					eventType = pull.next();
				}
			} catch (Exception e) {
				throw new XMLProccessException("Zpracování zdroje se nezdařilo.", e);
			}
		} catch (XMLProccessException e) {
			this.message = e.getMessage();
		}

		return messages;
	}

}