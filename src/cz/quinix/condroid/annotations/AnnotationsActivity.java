package cz.quinix.condroid.annotations;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cz.quinix.condroid.R;
import cz.quinix.condroid.URLBuilder;
import cz.quinix.condroid.conventions.Convention;

public class AnnotationsActivity extends ListActivity {
	private static final String SOURCE_URL = "http://condroid.quinix.cz/api/annotations";
	private List<Annotation> annotations = null;
	private Convention selectedCon = null;
	ProgressDialog pd;
	URLBuilder urlBuilder;
	private AnnotationsListAdapter adapter;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		
		this.urlBuilder = new URLBuilder(AnnotationsActivity.SOURCE_URL);
		
		Intent intent = this.getIntent();
		Convention selectedCon = (Convention) intent.getSerializableExtra("con");
		if(selectedCon != null) {
			this.selectedCon = selectedCon;
		}
		this.urlBuilder.addParam("cid", String.valueOf(this.selectedCon.cid));
		
		this.handleIntent(this.getIntent());
		this.adapter =  new AnnotationsListAdapter(this, R.layout.annotation_list_item, this.annotations);
		this.setListAdapter(this.adapter);
		

		this.setContentView(R.layout.annotation_list);
		if(this.selectedCon == null) {
			Toast.makeText(this, "Cannot load annotations, no convention selected.", Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		View footerView = this.getLayoutInflater().inflate(R.layout.annotation_list_footer, null);
		        this.getListView().addFooterView(footerView);
		
		
		this.getListView().setOnScrollListener(new EndlessScrollListener(this));
		

	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		this.setIntent(intent);
		this.handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      this.search(query);
	    } else {
	    	this.loadAnnotations();
	    }
		
	}
	
	private void search(String t) {
		this.urlBuilder.addParam("stub", t);
		this.loadAnnotations(true);
		this.adapter.setItems(this.annotations);
		this.adapter.notifyDataSetChanged();
	}

	private void loadAnnotations () {
		this.loadAnnotations(false);
	}
	private void loadAnnotations(boolean force) {
		
		if(this.annotations != null && !force) {
			return;
		}
		this.pd = ProgressDialog.show(AnnotationsActivity.this, "", "Načítám.", true);
		try {
			if(this.annotations != null) {
				this.annotations.clear();
			}
			else {
				this.annotations = new ArrayList<Annotation>();
			}
			this.annotations.addAll(new XMLLoader().execute(this.getUrl()).get());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.pd.dismiss();
		}
	}
	
	String getUrl() {
		return this.urlBuilder.getUrl();
	}
	

	private class AnnotationsListAdapter extends ArrayAdapter<Annotation> {

		private List<Annotation> items;

		public AnnotationsListAdapter(Context context, int textViewResourceId, List<Annotation> items) {
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
			Annotation it = null;
			try {
				it = items.get(position);
			} catch (IndexOutOfBoundsException e) {
				
			}
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


	public void addAnnotations(List<Annotation> list) {
		this.annotations.addAll(list);
		this.adapter.setItems(this.annotations);
		this.adapter.notifyDataSetChanged();
	}
	
	
}
