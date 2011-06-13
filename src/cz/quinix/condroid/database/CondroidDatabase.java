package cz.quinix.condroid.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class CondroidDatabase {
	public static final String TAG = "Condroid database";
	
	private static final String DATABASE_NAME = "condroid.db";
	private static final int DATABASE_VERSION = 1;
	
	private CondroidOpenHelper mDatabaseHelper;
	private static volatile CondroidDatabase instance = null;
	
	
	
	private CondroidDatabase(Context context) {
		mDatabaseHelper = new CondroidOpenHelper(context);
	}
	
	public static CondroidDatabase getInstance(Context context) {
		if(instance == null) {
			synchronized (CondroidDatabase.class) {
				if(instance == null) {
					instance = new CondroidDatabase(context);
				}
			}
		}
		return instance;
	}
	
	public Cursor query(String sql, String[] args) {
		Cursor cursor = mDatabaseHelper.getReadableDatabase().rawQuery(sql, args);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
	}
	
	
	
	class CondroidOpenHelper extends SQLiteOpenHelper {
		
		private static final String DATABASE_CREATE = 
			"CREATE TABLE \"annotations\" ( "+
				"\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
				"\"cid\"  INTEGER NOT NULL,"+
				"\"pid\"  INTEGER NOT NULL,"+
				"\"talker\"  TEXT(255) NOT NULL,"+
				"\"title\"  TEXT(255) NOT NULL,"+
				"\"length\"  TEXT(20),"+
				"\"time\"  TEXT(255),"+
				"\"annotation\"  TEXT,"+
				"\"lid\"  INTEGER NOT NULL,"+
				"\"type\"  TEXT(20) NOT NULL,"+
				"\"startTime\"  TEXT,"+
				"\"endTime\"  TEXT"+
			");" +
			"CREATE TABLE \"lines\" ("+
				"\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
				"\"cid\"  INTEGER NOT NULL,"+
				"\"title\"  TEXT(255) NOT NULL"+
			");";

		public CondroidOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
		
	}



	public boolean isEmpty() {
		Cursor c = this.query("SELECT count(*) FROM annotations", null);
		if(c.getInt(0) == 0) {
			return true;
		}
		return false;
	}
	
}
