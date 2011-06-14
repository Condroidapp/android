package cz.quinix.condroid.welcome;

import java.util.List;
import java.util.concurrent.ExecutionException;

import cz.quinix.condroid.R;
import cz.quinix.condroid.conventions.Convention;
import cz.quinix.condroid.conventions.ConventionLoader;
import cz.quinix.condroid.conventions.ConventionsActivity;
import cz.quinix.condroid.database.DataLoadTask;
import cz.quinix.condroid.database.DataProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class WelcomeActivity extends Activity {
	
	private DataProvider dataProvider = null;
	public static final String TAG = "Condroid";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.welcome);
		dataProvider = new DataProvider(getApplicationContext());
		if(!dataProvider.hasData()) {
			this.noDataDialog();
		}
	}
	
	private void noDataDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Datový soubor neobsahuje žádné záznamy. Aplikace potřebuje stáhnout cca 1 MB " +
				"zdrojových dat nutných pro její běh. Pokud nemáte datový tarif, připojte se nejdříve k WiFi. " +
				"Mimo tyto data se aplikace samovolně k internetu nepřipojuje.\nChcete pokračovat?")
				.setPositiveButton("Ano", new DialogInterface.OnClickListener() {
					public String[] items;
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						ProgressDialog pd = ProgressDialog.show(WelcomeActivity.this,"" ,"Načítám...", true);
						List<Convention> conventionList = null;
						try {
							conventionList = new ConventionLoader(WelcomeActivity.this, pd).execute().get();
						} catch (Exception e) {
							Log.e(WelcomeActivity.TAG, "", e);
							return;
						} 
						
						AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
						builder.setTitle("Vyberte con");
						items = new String[conventionList.size()];
						int i=0;
						for (Convention con : conventionList) {
							items[i++] = con.getName();
						}
						
						builder.setItems(items, new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_SHORT).show();
								
							}
						});
						AlertDialog d = builder.create();
						d.show();
						
						
						//ProgressDialog pd = ProgressDialog.show(WelcomeActivity.this,"" ,"Načítám...", true);
						//new DataLoadTask(pd, dataProvider).execute(null);
						
					}
				})
				.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
