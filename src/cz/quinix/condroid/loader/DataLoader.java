package cz.quinix.condroid.loader;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.util.Xml;
import cz.quinix.condroid.XMLProccessException;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;

public class DataLoader extends ListenedAsyncTask<String, String> {

	private ProgressDialog pd;
	private volatile DataProvider db;
	
	
	public DataLoader(AsyncTaskListener listener, ProgressDialog pd2, DataProvider dataProvider) {
		super(listener);
		this.pd = pd2;
		this.db = dataProvider;
	}

	@Override
	protected void onPostExecute(List<?> result) {
		
		super.onPostExecute(null);
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		
		super.onProgressUpdate(values);
		pd.setMessage(values[0]);
	}
	
	@Override
	protected List<?> doInBackground(String... params) {
		List<Annotation> messages = new ArrayList<Annotation>();
		XmlPullParser pull = Xml.newPullParser();
		Annotation annotation = null;
		try {
			try {
				URL url = new URL(params[0]);
				URLConnection conn = url.openConnection();

				pull.setInput(conn.getInputStream(), null);
				
			} catch (Exception ex) {
				throw new XMLProccessException("Stažení seznamu anotací se nezdařilo.", ex);
			}
			int eventType = 0;
			this.publishProgress("Stahuji...");
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
							if (name.equalsIgnoreCase("author")) {
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
							if (name.equalsIgnoreCase("start-time")) {
								annotation.setStartTime(pull.nextText());
							}
							if (name.equalsIgnoreCase("end-time")) {
								annotation.setEndTime(pull.nextText());
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
		//	this.message = e.getMessage();
		}
		this.publishProgress("Zpracovávám...");
		if(messages.size() > 0) {
			db.insert((List<Annotation>) messages);
		}
		return messages;
		
	}
	

}
