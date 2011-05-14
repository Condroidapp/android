package cz.quinix.condroid.annotations;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cz.quinix.condroid.R;
import cz.quinix.condroid.conventions.Convention;

public class AnnotationsActivity extends ListActivity {
	
	private List<Annotation> annotations = null;
	private Convention selectedCon = null;
	
	private static final String SOURCE_URL = "http://condroid.quinix.cz/api/annotations?cid=";
	private String actualSource = null; 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.selectedCon = (Convention) this.getIntent().getSerializableExtra("con");

		this.setContentView(R.layout.annotation_list);
		if(this.selectedCon == null) {
			Toast.makeText(this, "Cannot load annotations, no convention selected.", Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		this.actualSource = AnnotationsActivity.SOURCE_URL + this.selectedCon.cid;
		this.setListAdapter(new AnnotationsListAdapter(this, R.layout.annotation_list_item, this.loadAnnotations()));

	}
	
	private List<Annotation> loadAnnotations() {
		
		if(this.annotations != null) {
			return this.annotations;
		}
		
		try {
			this.annotations = new AnnotationsXMLLoader().execute(this.actualSource).get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
		
		return this.annotations;
	}

	private class AnnotationsListAdapter extends ArrayAdapter<Annotation> {

		private List<Annotation> items;

		public AnnotationsListAdapter(Context context, int textViewResourceId,
				List<Annotation> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.annotation_list_item, null);
			}

			Annotation it = items.get(position);
			if (it != null) {
				
				TextView tw = (TextView) v.findViewById(R.id.annotation_list_title);
				if (tw != null) {
					tw.setText(it.title);
				}
				TextView tw3 = (TextView) v.findViewById(R.id.annotation_list_info);
				if (tw != null) {
					tw3.setText(it.pid+", "+it.length+", "+it.programLine);
				}
				TextView tw2 = (TextView) v.findViewById(R.id.annotation_list_author);
				if (tw2 != null) {
					tw2.setText(it.talker);
				}
			}

			return v;
		}
		
		public AnnotationsListAdapter setItems(List<Annotation> c) {
			this.items = c;
			return this;
		}

	}
	
	private class AnnotationsXMLLoader extends AsyncTask<String, Integer, List<Annotation>> {
		ProgressDialog progress;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			this.progress = ProgressDialog.show(AnnotationsActivity.this, "", "Načítám.", true);
		}
		
		@Override
		protected void onPostExecute(List<Annotation> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			this.progress.cancel();
		}

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
}
