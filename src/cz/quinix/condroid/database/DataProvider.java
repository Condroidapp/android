package cz.quinix.condroid.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class DataProvider {
	
	public static String AUTHORITY = "cz.quinix.condroid.database.DataProvider";
	public static Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY + "/database");
		
	
	private CondroidDatabase mDatabase;
	
	public DataProvider(Context context) {
		mDatabase = CondroidDatabase.getInstance(context);
	}
	
	public boolean hasData() {
		return !mDatabase.isEmpty();
	}
	
	

	
	
}
