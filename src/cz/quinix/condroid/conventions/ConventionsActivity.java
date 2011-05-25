package cz.quinix.condroid.conventions;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
	private static final String list_url = "http://condroid.quinix.cz/api/con-list";
	ProgressDialog pd;
	
	private List<Convention> cons;
	private ConventionListAdapter c;
	static int imageSize;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		imageSize = (int) (this.getWindowManager().getDefaultDisplay().getWidth()/9);
		this.loadCons();
		c = new ConventionListAdapter(this, R.layout.cons_list, this.cons);
		this.setListAdapter(c);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflanter = this.getMenuInflater();
		inflanter.inflate(R.menu.conventions_list, menu);
		
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
		
		
		try {
			this.cons = new XMLLoader().execute(ConventionsActivity.list_url).get();
		} catch (Exception ex) {
			Toast.makeText(this, "Can't load conventions list.",
					Toast.LENGTH_LONG).show();

		} finally {
			this.pd.dismiss();
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(position < this.cons.size()) {
			Convention selected = (Convention) l.getItemAtPosition(position);
			Intent intent = new Intent(this, AnnotationsActivity.class);
			intent.putExtra("con", selected);
			this.startActivity(intent);
		}
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
					Bitmap b = it.getImage();
					if(imageSize > 0) {
						Bitmap bi = Bitmap.createScaledBitmap(b, imageSize, imageSize, true);
					    
					    iv.setImageBitmap(bi);
					}
					
				}
				TextView tw = (TextView) v.findViewById(R.id.convention_list_item_text);
				if (tw != null) {
					tw.setText(it.name);
				}
				TextView tw2 = (TextView) v.findViewById(R.id.convention_list_item_date);
				if (tw2 != null) {
					tw2.setText(it.date);
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