package cz.quinix.condroid.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CondroidDatabase {
	public static final String TAG = "Condroid database";
	
	private static final String DATABASE_NAME = "condroid.db";
	private static final int DATABASE_VERSION = 5;
	public static final String CON_TABLE = "cons";
	public static final String ANNOTATION_TABLE = "annotations";
	public static final String LINE_TABLE = "lines";
	public static final String FAVORITE_TABLE = "favorite_program";
	
	private CondroidOpenHelper mDatabaseHelper;
	
	
	
	public CondroidDatabase(Context context) {
		mDatabaseHelper = new CondroidOpenHelper(context);
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
	
	public Cursor query(String sql) {
		return query(sql, null);
	}
	
	public Cursor query (String table, String[] columns, String condition, String[] conditionArgs, String orderBy, String limit) {
		return query(table, columns, condition, conditionArgs, orderBy, limit, false, null);
	}
	
	public Cursor query (String table, String[] columns, String condition, String[] conditionArgs, String orderBy, String limit, boolean distinct, String groupBy) {
		return this.mDatabaseHelper.getReadableDatabase().query(distinct, table, columns, condition, conditionArgs, groupBy, null, orderBy, limit);
	}
	
	
	
	static class CondroidOpenHelper extends SQLiteOpenHelper {
		
		
		
		private static final String DATABASE_CREATE_CONS = 
			"CREATE TABLE \""+ CON_TABLE +"\" ( "+
			"\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
			"\"name\"  TEXT(255)NOT NULL,"+
			"\"date\"  TEXT(255) NOT NULL,"+
			"\"iconUrl\"  TEXT(255) NOT NULL,"+
			"\"dataUrl\"  TEXT(255)," +
			"\"message\" TEXT," + 
			"\"has_annotations\" INTEGER," + 
			"\"has_timetable\" INTEGER," + 
			
			"\"locationsFile\" TEXT"+
			");";
			private static final String DATABASE_CREATE_ANNOTATIONS = 	"CREATE TABLE \""+ ANNOTATION_TABLE +"\" ( "+
				"\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
				"\"cid\"  INTEGER NOT NULL,"+
				"\"pid\"  TEXT(20) NOT NULL,"+
				"\"talker\"  TEXT(255) NOT NULL,"+
				"\"title\"  TEXT(255) NOT NULL,"+
				"\"length\"  TEXT(20),"+
				"\"time\"  TEXT(255),"+
				"\"annotation\"  TEXT,"+
				"\"lid\"  INTEGER NOT NULL,"+
				"\"location\"  TEXT(100) NULL,"+
				"\"mainType\"  TEXT(1) NOT NULL,"+
				"\"additionalTypes\"  TEXT(20) NOT NULL,"+
				"\"startTime\"  TEXT NULL,"+
				"\"endTime\"  TEXT NULL"+
			");";
			private static final String DATABASE_CREATE_LINES = 	"CREATE TABLE \"" +LINE_TABLE+ "\" ("+
				"\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
				"\"cid\"  INTEGER NOT NULL,"+
				"\"title\"  TEXT(255) NOT NULL"+
			");";
			private static final String DATABASE_CREATE_FAVORITE = 	"CREATE TABLE IF NOT EXISTS \"" +FAVORITE_TABLE+ "\" ("+
					"\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
					"\"pid\"  INTEGER NOT NULL"+
			");";
			

		public CondroidOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			//Log.w("D", "Version:" +DATABASE_VERSION);	
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL(DATABASE_CREATE_CONS);
			db.execSQL(DATABASE_CREATE_ANNOTATIONS);
			db.execSQL(DATABASE_CREATE_LINES);
			db.execSQL(DATABASE_CREATE_FAVORITE);
		}
		

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
			if(oldVersion < DATABASE_VERSION && oldVersion <= 5) {
				db.execSQL("DROP TABLE "+CON_TABLE);
				db.execSQL("DROP TABLE "+ANNOTATION_TABLE);
				db.execSQL("DROP TABLE "+LINE_TABLE);
				if(oldVersion > 4)
					db.execSQL("DROP TABLE "+FAVORITE_TABLE);
				this.onCreate(db);
			}
		}
		
		
		
	}



	public boolean isEmpty() {
		Cursor c = this.query("SELECT count(*) FROM annotations", null);
		if(c.getInt(0) == 0) {
			c.close();
			return true;
		}
		c.close();
		return false;
	}

	public void purge() {
		SQLiteDatabase db = this.mDatabaseHelper.getWritableDatabase(); 
		db.execSQL("DROP TABLE "+CON_TABLE);
		db.execSQL("DROP TABLE "+ANNOTATION_TABLE);
		db.execSQL("DROP TABLE "+LINE_TABLE);
		this.mDatabaseHelper.onCreate(db);
	}
	
	SQLiteDatabase getWritableDatabase() {
		return mDatabaseHelper.getWritableDatabase();
	}
	
}
