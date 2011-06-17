package cz.quinix.condroid.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import cz.quinix.condroid.annotations.Annotation;
import cz.quinix.condroid.conventions.Convention;
import cz.quinix.condroid.welcome.WelcomeActivity;

public class DataProvider {
	
	public static String AUTHORITY = "cz.quinix.condroid.database.DataProvider";
	public static Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY + "/database");
	public static int ITEMS_PER_PAGE = 40;
		
	
	private CondroidDatabase mDatabase;
	private Convention con;
	private static volatile DataProvider instance;
	
	private DataProvider(Context context) {
		mDatabase = new CondroidDatabase(context);
	}
	
	public static DataProvider getInstance(Context context) {
		if(instance == null) {
			synchronized (CondroidDatabase.class) {
				if(instance == null) {
					instance = new DataProvider(context);
				}
			}
		}
		return instance;
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

	public List<Annotation> getAnnotations(String object, int page) {
		List<Annotation> ret = new ArrayList<Annotation>();
		String condition = "";
		if(object != null) {
			condition = "pid LIKE '%"+object+"%' OR title LIKE '%"+object+"%'";
		}
		
		Cursor c = this.mDatabase.query(CondroidDatabase.ANNOTATION_TABLE, null, condition, null, "title ASC", "0,"+(page*ITEMS_PER_PAGE));
		
		while(c.moveToNext()) {
			
			Annotation annotation = new Annotation();
			annotation.setPid(c.getString(c.getColumnIndex("pid")));
			annotation.setTitle(c.getString(c.getColumnIndex("title")));
			
			ret.add(annotation);
		}
		return ret;
		
	}
	
	

	
	
}
