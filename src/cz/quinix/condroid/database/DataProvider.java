package cz.quinix.condroid.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import cz.quinix.condroid.ProgramLine;
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
	
	private static HashMap<Integer, String> programLines = null;
	
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
			programLines = null;
		}
		try {
			mDatabase.insert(con, result);
		} catch (Exception ex) {
			Log.w(WelcomeActivity.TAG, ex);
			mDatabase.purge();
		}
	}

	public List<Annotation> getAnnotations(String condition, int page) {
		List<Annotation> ret = new ArrayList<Annotation>();
		
		
		Cursor c = this.mDatabase.query(CondroidDatabase.ANNOTATION_TABLE, null, condition, null, "title ASC", (page*ITEMS_PER_PAGE) + ","+ ITEMS_PER_PAGE);
		
		while(c.moveToNext()) {
			
			Annotation annotation = new Annotation();
			annotation.setPid(c.getString(c.getColumnIndex("pid")));
			annotation.setTitle(c.getString(c.getColumnIndex("title")));
			annotation.setAnnotation(c.getString(c.getColumnIndex("annotation")));
			annotation.setAuthor(c.getString(c.getColumnIndex("talker")));
			annotation.setEndTime(c.getString(c.getColumnIndex("endTime")));
			annotation.setLength(c.getString(c.getColumnIndex("length")));
			annotation.setLid(c.getInt(c.getColumnIndex("lid")));
			annotation.setStartTime(c.getString(c.getColumnIndex("startTime")));
			annotation.setType(c.getString(c.getColumnIndex("type")));
			ret.add(annotation);
		}
		return ret;
	}
	
	public ProgramLine getProgramLine (int lid) {
		ProgramLine pl = new ProgramLine();
		
		if(programLines == null) {
			this.loadProgramLines();
		}
		if(programLines.containsKey(lid)) {
			pl.setLid(lid);
			pl.setName(programLines.get(lid));
		}
		
		return pl;
	}

	public HashMap<Integer, String> getProgramLines() {
		if(programLines == null) {
			this.loadProgramLines();
		}
		return programLines;
	}
	
	private void loadProgramLines() {
		programLines = new HashMap<Integer, String>();
		
		Cursor c = this.mDatabase.query(CondroidDatabase.LINE_TABLE, null, null, null, "title ASC", null);
		while(c.moveToNext()) {
			programLines.put(c.getInt(c.getColumnIndex("id")), c.getString(c.getColumnIndex("title")));
		}
	}
	
	

	
	
}
