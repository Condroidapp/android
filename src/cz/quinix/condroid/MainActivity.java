package cz.quinix.condroid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity {
    /** Called when the activity is first created. */
	private static String list_url = "http://condroid/xml/cons.xml";
	static final String[] COUNTRIES = new String[] {
		    "Afghanistan", "Albania"
		  };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.cons_list, this.loadCons()));
        
        
    }
    
    private String[] loadCons() {
    	try {
    		URL url = new URL(MainActivity.list_url);
    		URLConnection conn = url.openConnection();
    		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    		String line = "";
           // while ((line = rd.readLine()) != null) {
        	//	System.out.println(line);
                //GetWebPage.this.h.sendMessage(lmsg);
           // }
    	} catch (Exception ex) {
    		Toast.makeText(this, "Can't load conventions list.", Toast.LENGTH_LONG).show();
    		
    	}
    	
    	return COUNTRIES;
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflanter = this.getMenuInflater();
    	inflanter.inflate(R.menu.main, menu);
    	return true;
    }
}