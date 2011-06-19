package cz.quinix.condroid.annotations;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;

public class AllAnotations extends ListActivity {

	private EndlessAdapter adapter;
	private DataProvider provider;
	private String search = null;
	private List<Annotation> annotations;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.provider = DataProvider.getInstance(getApplicationContext());
		this.handleIntent(this.getIntent());
		annotations = this.provider.getAnnotations(search, 0);
		this.adapter = new EndlessAdapter(annotations);
		this.setListAdapter(this.adapter);

		this.setContentView(R.layout.annotation_list);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		this.setIntent(intent);
		this.handleIntent(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = this.getMenuInflater();
		i.inflate(R.menu.annotations, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

		@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.annotations_refresh:
			this.search = null;
			// refresh deletes search term and page
			this.annotations.clear();
			this.annotations.addAll(this.provider.getAnnotations(search, 0));
			this.adapter.refreshDataset();

			return true;
		case R.id.search:
			onSearchRequested();
			return true;
		case R.id.lineFilter:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.dPickLine);
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Annotation selected = (Annotation) l.getItemAtPosition(position);
		Intent intent = new Intent(this, ShowAnnotation.class);
		intent.putExtra("annotation", selected);
		this.startActivity(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			search(intent.getStringExtra(SearchManager.QUERY));
		}
	}

	private void search(String t) {
		search = t;
		List<Annotation> foo = new ArrayList<Annotation>();
		//foo.addAll(this.annotations);
		foo = this.provider.getAnnotations(search, 0);
		if (foo.size() > 0) {
			this.annotations.clear();
			this.annotations.addAll(foo);
		} else {
			Toast.makeText(this,
					"Nebyly nalezeny žádné anotace obsahující '" + t + "'",
					Toast.LENGTH_LONG).show();
		}

		this.adapter.refreshDataset();
	}

	class EndlessAdapter extends com.commonsware.cwac.endless.EndlessAdapter {
		private RotateAnimation rotate;
		private List<Annotation> itemsToAdd;
		private int itemsPerPage = 0;
		
		public EndlessAdapter(List<Annotation> items) {
			super(new ArrayAdapter<Annotation>(AllAnotations.this,
					R.layout.annotation_list_item, android.R.id.text1, items));
			rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
					0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			rotate.setDuration(1000);
			rotate.setRepeatMode(Animation.RESTART);
			rotate.setRepeatCount(Animation.INFINITE);
			this.itemsPerPage = items.size();
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			if((this.getCount()-1) % this.itemsPerPage != 0) {
				return false;
			}
			this.itemsToAdd = provider.getAnnotations(search, (int) (this.getCount() /  this.itemsPerPage));
			
			return(this.itemsToAdd.size() == this.itemsPerPage);
		}

		@Override
		protected void appendCachedData() {
			if(this.itemsToAdd != null && this.itemsToAdd.size() > 0) {
				@SuppressWarnings("unchecked")
				ArrayAdapter<Annotation> a = (ArrayAdapter<Annotation>) this.getWrappedAdapter();
				for (int i = 0; i<this.itemsToAdd.size(); i++) {
					a.add(this.itemsToAdd.get(i));
				}
				this.itemsToAdd = null;
			}

		}
		
		@Override
		protected View getPendingView(ViewGroup parent) {
			//TODO
			View row=getLayoutInflater().inflate(R.layout.row, null);
			
			View child=row.findViewById(R.id.throbber);
			child.startAnimation(rotate);
			
			return(row);
		}
		
		@Override
		public void refreshDataset() {
			super.refreshDataset();
			AllAnotations.this.getListView().setSelection(0);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.annotation_list_item, null);
			}
			Annotation it = null;
			try {
				it = (Annotation) this.getItem(position);
			} catch (IndexOutOfBoundsException e) {

			}
			if (it != null) {

				TextView tw = (TextView) v
						.findViewById(R.id.annotation_list_title);
				if (tw != null) {
					tw.setText(it.getTitle());
				}
				TextView tw3 = (TextView) v
						.findViewById(R.id.annotation_list_info);
				if (tw != null) {
					tw3.setText(provider.getProgramLine(it.getLid()).getName());
				}
				TextView tw2 = (TextView) v
						.findViewById(R.id.annotation_list_author);
				if (tw2 != null) {
					tw2.setText(it.getPid() + ", " +it.getAuthor());
				}
				return v;
			}
			return super.getView(position, convertView, parent);
		}

	}
}
