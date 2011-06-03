package cz.quinix.condroid.conventions;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cz.quinix.condroid.CondroidActivity;
import cz.quinix.condroid.R;
import cz.quinix.condroid.annotations.AnnotationsActivity;

public class ConventionsActivity extends CondroidActivity {
	/** Called when the activity is first created. */
	private static final String list_url = "http://condroid.fan-project.com/api/con-list";
	ProgressDialog pd;
	
	private List<Convention> cons;
	private ConventionListAdapter c;
	static int imageSize;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		imageSize = (int) (this.getWindowManager().getDefaultDisplay().getWidth()/9);
		this.loadCons();
		
		SharedPreferences settings = this.getSharedPreferences(PREF_NAME, 0);
		int selectedCon = settings.getInt("selectedCon", 0);
		
		if(selectedCon > 0) {
			for(int i = 0; i<this.cons.size(); i++) {
				Convention con = this.cons.get(i);
				if(con.getCid() == selectedCon) {
					Toast.makeText(this, String.valueOf(con.getName()), Toast.LENGTH_SHORT).show();
					this.startAnnotationActivity(con);

					this.finish();
				}
			}
		}
		
		c = new ConventionListAdapter(this, R.layout.cons_list, this.cons);
		this.setListAdapter(c);
		

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflanter = this.getMenuInflater();
		inflanter.inflate(R.menu.conventions, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.cons_refresh:
			this.cons = null;
			this.loadCons();
			this.c.setItems(this.cons).notifyDataSetChanged();
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void loadCons() {
		if(this.cons != null) {
			return;
		}
		this.pd = ProgressDialog.show(ConventionsActivity.this, "", "Načítám.", true);
		this.cons = new ArrayList<Convention>();
		
		try {
			this.cons = new XMLLoader(this).execute(ConventionsActivity.list_url).get();
		} catch (Exception ex) {
			Toast.makeText(this, ex.getMessage(),
					Toast.LENGTH_LONG).show();
			
		} finally {
			this.pd.dismiss();
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(position < this.cons.size()) {
			Convention selected = (Convention) l.getItemAtPosition(position);
			
			SharedPreferences settings = this.getSharedPreferences(PREF_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("selectedCon", selected.getCid());
			editor.commit();
			
			
			this.startAnnotationActivity(selected);
		}
	}
	
	private void startAnnotationActivity (Convention con) {
		Intent intent = new Intent(this, AnnotationsActivity.class);
		intent.putExtra("con", con);
		this.startActivity(intent);	
	}

	private class ConventionListAdapter extends ArrayAdapter<Convention> {

		private List<Convention> items;
		
		public ConventionListAdapter(Context context, int textViewResourceId,
				List<Convention> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.convention_list_item, null);
			}
			Convention it = null;
			try {
				it = items.get(position);
			} catch (IndexOutOfBoundsException e) {
				// TODO: handle exception
			}
			if (it != null) {
				
				ImageView iv = (ImageView) v.findViewById(R.id.convention_list_item_image);
				if (iv != null) {
					try {
					Bitmap b = it.getImage();
					if(imageSize > 0) {
						Bitmap bi = Bitmap.createScaledBitmap(b, imageSize, imageSize, true);
					    
					    iv.setImageBitmap(bi);
					}
					} catch (NullPointerException e) {
						iv.setVisibility(View.INVISIBLE);
					}
					
				}
				TextView tw = (TextView) v.findViewById(R.id.convention_list_item_text);
				if (tw != null) {
					tw.setText(it.getName());
				}
				TextView tw2 = (TextView) v.findViewById(R.id.convention_list_item_date);
				if (tw2 != null) {
					tw2.setText(it.getDate());
				}
			}

			return v;
		}
		
		public ConventionListAdapter setItems(List<Convention> c) {
			this.items = c;
			return this;
		}

	}
	
}