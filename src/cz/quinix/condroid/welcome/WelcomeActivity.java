package cz.quinix.condroid.welcome;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cz.quinix.condroid.R;
import cz.quinix.condroid.RunningActivity;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.annotations.AllAnotations;
import cz.quinix.condroid.conventions.Convention;
import cz.quinix.condroid.conventions.ConventionLoader;
import cz.quinix.condroid.database.DataLoadTask;
import cz.quinix.condroid.database.DataProvider;

public class WelcomeActivity extends CondroidActivity implements AsyncTaskListener {

	private DataProvider dataProvider = null;
	public static final String TAG = "Condroid";
	private ProgressDialog pd;
	private List<Convention> conventionList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.welcome);
		dataProvider = DataProvider.getInstance(getApplicationContext());
		if (!dataProvider.hasData()) {
			this.noDataDialog(getString(R.string.noData)+" "+getString(R.string.downloadDialog));
		}
		
		Button refresh = (Button) this.findViewById(R.id.bReload); 
		refresh.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				noDataDialog(getString(R.string.downloadDialog));
				
			}
		});
		
		Button all = (Button) findViewById(R.id.bShowAll);
		all.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(WelcomeActivity.this, AllAnotations.class);
				startActivity(intent);
				
			}
		});
		
		Button now = (Button) findViewById(R.id.bShowActual);
		now.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(WelcomeActivity.this, RunningActivity.class);
				startActivity(intent);
				
			}
		});
	}

	private void noDataDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
				.setPositiveButton(R.string.yes, new DialogOnClick())
				.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@SuppressWarnings("unchecked")
	public void onAsyncTaskCompleted(List<?> list) {
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}

		if (list != null) {
			// conventions downloaded
			conventionList = (List<Convention>) list;
			AlertDialog.Builder builder = new AlertDialog.Builder(
					WelcomeActivity.this);
			builder.setTitle(R.string.dPickConvention);
			String[] items = new String[list.size()];
			int i = 0;
			for (Object con : list) {
				items[i++] = ((Convention) con).getName();
			}

			builder.setItems(items, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {

					dataProvider.setConvention(conventionList.get(which));
					pd = ProgressDialog.show(WelcomeActivity.this, "",
							getString(R.string.preparing), true);
					new DataLoadTask(WelcomeActivity.this, pd, dataProvider)
							.execute(conventionList.get(which).getDataUrl());

				}
			});
			AlertDialog d = builder.create();
			d.show();
		}
	}

	class DialogOnClick implements DialogInterface.OnClickListener {

		public void onClick(DialogInterface dialog, int which) {

			dialog.cancel();
			pd = ProgressDialog.show(WelcomeActivity.this, "", getString(R.string.loading),
					true);

			try {
				new ConventionLoader(WelcomeActivity.this).execute();
			} catch (Exception e) {
				Log.e(WelcomeActivity.TAG, "", e);
				return;
			}

		}

	}
}
