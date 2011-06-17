package cz.quinix.condroid.database;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import cz.quinix.condroid.annotations.Annotation;
import cz.quinix.condroid.conventions.Convention;
import cz.quinix.condroid.welcome.WelcomeActivity;

public class DataProvider {
	
	public static String AUTHORITY = "cz.quinix.condroid.database.DataProvider";
	public static Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY + "/database");
		
	
	private CondroidDatabase mDatabase;
	private Convention con;
	
	public DataProvider(Context context) {
		mDatabase = CondroidDatabase.getInstance(context);
	}
	
	public boolean hasData() {
		return !mDatabase.isEmpty();
	}

	public void setConvention(Convention convention) {
		con = convention;		
	}

	public void insert(List<Annotation> result) {
		if(!mDatabase.isEmpty()) {
			mDatabase.purge();
		}
		try {
			mDatabase.insert(con, result);
		} catch (Exception ex) {
			Log.w(WelcomeActivity.TAG, ex);
			mDatabase.purge();
		}
	}
	
	

	
	
}
