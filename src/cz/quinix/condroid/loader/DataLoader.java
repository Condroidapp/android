package cz.quinix.condroid.loader;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.text.Html;
import android.util.Log;
import android.util.Xml;
import cz.quinix.condroid.XMLProccessException;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.model.Annotation;

public class DataLoader extends ListenedAsyncTask<String, Integer> {

	private ProgressDialog pd;
	
	public DataLoader(AsyncTaskListener listener, ProgressDialog pd2) {
		super(listener);
		this.pd = pd2;
	}
	
	@Override
	protected void onPostExecute(List<?> result) {
		// TODO Auto-generated method stub
		pd.dismiss();
		super.onPostExecute(result);
		
	}

	@Override
	protected void onProgressUpdate(Integer... values) {		
		super.onProgressUpdate(values);
		int value = values[0];
		if(value == -1) {
			
			ProgressDialog pd = new ProgressDialog(this.pd.getContext());
			this.pd.dismiss();
			this.pd = pd;
			pd.setMessage("Stahuji...");
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}
		else if(values.length == 2) {
			pd.setMax(values[1]);			
			pd.show();
		}
		else {
			pd.setProgress(value);
		}
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
			this.publishProgress(-1);
			try {
				eventType = pull.getEventType();
			} catch (XmlPullParserException e) {
				throw new XMLProccessException("Zpracování seznamu anotací se nezdařilo", e);
			}
			try {
				int counter = 0;
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						break;
					
					

					case XmlPullParser.START_TAG:
						String name = pull.getName();
						if(name.equalsIgnoreCase("annotations")) {
							if(pull.getAttributeCount() > 0) {
								if(pull.getAttributeName(0).equals("count")) {
									try {
										this.publishProgress(0, Integer.parseInt(pull.getAttributeValue(0)));
									} catch (NumberFormatException e) {
									}
								}
							}
						}
						if (name.equalsIgnoreCase("programme")) {
							annotation = new Annotation();
						} else {
							if (name.equalsIgnoreCase("pid")) {
								annotation.setPid(pull.nextText().trim());
							}
							if (name.equalsIgnoreCase("author")) {
								annotation.setAuthor(pull.nextText().trim());
							}
							if (name.equalsIgnoreCase("title")) {
								annotation.setTitle(Html.fromHtml(pull.nextText().trim()).toString());
							}
							if (name.equalsIgnoreCase("type")) {
								annotation.setType(pull.nextText().trim());
							}
							if (name.equalsIgnoreCase("program-line")) {
								annotation.setProgramLine(pull.nextText().trim());
							}
							if (name.equalsIgnoreCase("location")) {
								annotation.setLocation(pull.nextText().trim());
							}
							if (name.equalsIgnoreCase("annotation")) {
								annotation.setAnnotation(Html.fromHtml(pull.nextText().trim()).toString());
							}
							if (name.equalsIgnoreCase("start-time")) {
								annotation.setStartTime(pull.nextText().trim());
							}
							if (name.equalsIgnoreCase("end-time")) {
								annotation.setEndTime(pull.nextText().trim());
							}
						}
						break;

					case XmlPullParser.END_TAG:
						name = pull.getName();
						if (name.equalsIgnoreCase("programme")
								&& annotation != null) {
							messages.add(annotation);
							this.publishProgress(counter++);
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
			Log.e("Condroid","Exception during XML data recieve.", e);
			throw e;
		}

		return messages;
		
	}
	

}
