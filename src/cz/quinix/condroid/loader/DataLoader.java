package cz.quinix.condroid.loader;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.CondroidActivity;
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

	//private ProgressDialog pd;
    private String pdString;
    private int pdMax;
    //private int pdActual;
	
	public DataLoader(AsyncTaskListener listener) {
		super(listener);
        pdString = parentActivity.getString(R.string.loading);
		this.showDialog();
	}
	
	@Override
	protected void onPostExecute(List<?> result) {
		pd.dismiss();
		super.onPostExecute(result);
		
	}

	@Override
	protected void onProgressUpdate(Integer... values) {		
		super.onProgressUpdate(values);
		int value = values[0];
		if(values.length == 2) {
            this.pd.dismiss();
            pdString = parentActivity.getString(R.string.downloading);
            this.pdMax = values[1];
            this.showDialog();
		}
		else if(pd.getMax() > 0) {
			pd.setProgress(value);
		}
	}

    @Override
    protected void showDialog() {
        if(this.pd != null && this.pd.isShowing()) {
            pd.dismiss();
        }
        if(parentActivity != null) {
            this.pd = new ProgressDialog(parentActivity);
            this.pd.setMessage(pdString);
            if(this.pdMax > 0) {
                this.pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setMax(pdMax);
            }
            pd.show();
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
                                        int x= Integer.parseInt(pull.getAttributeValue(0));
                                        if(x >0)
										    this.publishProgress(0, x);
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
