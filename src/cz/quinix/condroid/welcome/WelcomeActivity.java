package cz.quinix.condroid.welcome;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class WelcomeActivity extends Activity {
	private DataProvider dataProvider = null;
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
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
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
