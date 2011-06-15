package cz.quinix.condroid.database;

import java.util.List;

import cz.quinix.condroid.annotations.Annotation;
import cz.quinix.condroid.conventions.Convention;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

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
		mDatabase.insert(con, result);
	}
	
	

	
	
}
