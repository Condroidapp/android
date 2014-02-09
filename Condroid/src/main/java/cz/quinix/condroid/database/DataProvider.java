package cz.quinix.condroid.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.model.ProgramLine;
import cz.quinix.condroid.model.Reminder;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DataProvider {

    public static String AUTHORITY = "cz.quinix.condroid.database.DataProvider";
    public static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/database");
    public static int ITEMS_PER_PAGE = 40;


    private CondroidDatabase mDatabase;
    private Convention con;
    private List<Integer> favorited;
    private static volatile DataProvider instance;

    private static HashMap<Integer, String> programLines = null;

    private DataProvider(Context context) {
        mDatabase = new CondroidDatabase(context);
    }

    public static DataProvider getInstance(Context context) {
        if (instance == null) {
            synchronized (CondroidDatabase.class) {
                if (instance == null) {
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

    public DatabaseLoader prepareInsert(boolean fullInsert) {
        if (fullInsert && !mDatabase.isEmpty()) {
            mDatabase.purge(con.getCid());
        }
        programLines = null;
        favorited = null;

        return new DatabaseLoader(null, mDatabase, con, fullInsert);
    }

    public List<Annotation> getAnnotations(SearchQueryBuilder con, int skip) {
        List<Annotation> ret = new ArrayList<Annotation>();
        String condition = null;
        if (con != null)
            condition = con.buildCondition();

        Cursor c = this.mDatabase.query(CondroidDatabase.ANNOTATION_TABLE, null, condition, null, "startTime ASC, lid ASC, title ASC", (skip) + "," + ITEMS_PER_PAGE);

        while (c.moveToNext()) {


            ret.add(readAnnotation(c));
        }
        c.close();
        return ret;
    }

    public ProgramLine getProgramLine(int lid) {
        ProgramLine pl = new ProgramLine();

        if (programLines == null) {
            this.loadProgramLines();
        }
        if (programLines != null) {
            if (programLines.containsKey(lid)) {
                pl.setLid(lid);
                pl.setName(programLines.get(lid));
            }
        }

        return pl;
    }

    public HashMap<Integer, String> getProgramLines() {
        if (programLines == null) {
            this.loadProgramLines();
        }
        return programLines;
    }

    private void loadProgramLines() {
        programLines = new HashMap<Integer, String>();

        Cursor c = this.mDatabase.query(CondroidDatabase.LINE_TABLE, null, null, null, "title ASC", null);
        while (c.moveToNext()) {
            programLines.put(c.getInt(c.getColumnIndex("id")), c.getString(c.getColumnIndex("title")));
        }
        c.close();
    }

    public List<Date> getDates() {

        Cursor c = this.mDatabase.query("SELECT DISTINCT STRFTIME('%Y-%m-%d',startTime) AS sDate FROM " + CondroidDatabase.ANNOTATION_TABLE + " ORDER by STRFTIME('%Y-%m-%d',startTime) ASC");

        List<Date> map = new ArrayList<Date>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if (c.getCount() > 0) {
            do {
                try {
                    if (c.getString(c.getColumnIndex("sDate")) != null) {
                        map.add(df.parse(c.getString(c.getColumnIndex("sDate"))));
                    }
                } catch (ParseException e) {
                    Log.e("Condroid", "Exception when parsing dates for list dialog.", e);
                }
            } while (c.moveToNext());

        }
        c.close();

        return map;
    }


    public List<Annotation> getRunningAndNext(SearchQueryBuilder sb, int skip) {
        List<Annotation> l = new ArrayList<Annotation>();
        String condition = "";
        if (sb != null) {
            condition = sb.buildCondition();
        }

        if (skip == 0) {
            Cursor c = this.mDatabase.query(CondroidDatabase.ANNOTATION_TABLE, null, "startTime < DATETIME('now') AND endTime > DATETIME('now')" + (condition != null && !condition.equals("") ? " AND " + condition : ""), null, "startTime DESC", null, false);
            while (c.moveToNext()) {
                Annotation annotation = readAnnotation(c);

                l.add(annotation);

            }
            c.close();
        }

        Cursor c2 = this.mDatabase.query(CondroidDatabase.ANNOTATION_TABLE, null, "startTime > DATETIME('now')" + (condition != null && !condition.equals("") ? " AND " + condition : ""), null, "startTime ASC, lid ASC", (skip) + "," + ITEMS_PER_PAGE, false);
        Log.d("Condroid", "LIMIT: " + ((skip) + "," + ITEMS_PER_PAGE) + ", returned items " + c2.getCount());

        while (c2.moveToNext()) {
            Annotation annotation = readAnnotation(c2);
            l.add(annotation);
        }
        c2.close();

        return l;
    }

    private Annotation readAnnotation(Cursor c) {
        Annotation annotation = new Annotation();
        annotation.setPid(c.getString(c.getColumnIndex("pid")));
        annotation.setTitle(c.getString(c.getColumnIndex("title")));
        annotation.setAnnotation(c.getString(c.getColumnIndex("annotation")));
        annotation.setAuthor(c.getString(c.getColumnIndex("talker")));
        annotation.setSQLEndTime(c.getString(c.getColumnIndex("endTime")));
        //annotation.setLength(c.getString(c.getColumnIndex("length")));
        annotation.setLocation(c.getString(c.getColumnIndex("location")));
        annotation.setLid(c.getInt(c.getColumnIndex("lid")));
        annotation.setSQLStartTime(c.getString(c.getColumnIndex("startTime")));
        annotation.setType(c.getString(c.getColumnIndex("mainType")));
        annotation.setAdditonalTypes(c.getString(c.getColumnIndex("additionalTypes")));
        return annotation;
    }

    public Convention getCon() {
        if (this.con != null) {
            return con;
        }
        Cursor c = this.mDatabase.query(CondroidDatabase.CON_TABLE, null, null, null, null, null);
        Convention co = new Convention();
        while (c.moveToNext()) {
            co.setCid(c.getInt(c.getColumnIndex("id")));
            co.setDataUrl(c.getString(c.getColumnIndex("dataUrl")));
            co.setDate(c.getString(c.getColumnIndex("date")));
            co.setIconUrl(c.getString(c.getColumnIndex("iconUrl")));
            co.setName(c.getString(c.getColumnIndex("name")));
            co.setMessage(c.getString(c.getColumnIndex("message")));
            co.setLocationsFile(c.getString(c.getColumnIndex("locationsFile")));
            DateTimeFormatter format = DateTimeFormat
                    .forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();
            try {
                co.setLastUpdate(format.parseDateTime(c.getString(c.getColumnIndex("lastUpdate"))).toDate());
            } catch (Exception e) {
                Log.e("Condroid", "Parsing DB date", e);
                co.setLastUpdate(new Date());
            }
        }
        c.close();
        this.con = co;
        return co;
    }

    public List<Integer> getFavorited() {
        if (favorited != null) {
            return favorited;
        }
        Cursor c = this.mDatabase.query(CondroidDatabase.FAVORITE_TABLE, null, null, null, "pid ASC", null);
        favorited = new ArrayList<Integer>();

        while (c.moveToNext()) {
            favorited.add(c.getInt(c.getColumnIndex("pid")));
        }
        return favorited;
    }

    public boolean doFavorite(int pid) {
        Cursor c = this.mDatabase.query(CondroidDatabase.FAVORITE_TABLE, null, "pid=" + pid, null, null, null);
        favorited = null;
        if (c.getCount() > 0) {
            this.mDatabase.query("DELETE FROM " + CondroidDatabase.FAVORITE_TABLE + " WHERE pid = '" + pid + "'");
            return false;
        } else {
            this.mDatabase.query("INSERT INTO " + CondroidDatabase.FAVORITE_TABLE + " (pid) VALUES ('" + pid + "')");
            return true;
        }
    }

    public boolean setReminder(Annotation annotation, int i) {
        try {
            Cursor c = this.mDatabase.query("SELECT pid FROM " + CondroidDatabase.ANNOTATION_TABLE + " WHERE pid =" + annotation.getPid() + " AND startTime IS NOT NULL");
            if (c.getCount() > 0) {
                this.mDatabase.query("DELETE FROM " + CondroidDatabase.REMINDER_TABLE + " WHERE pid=" + annotation.getPid());
                this.mDatabase.query("INSERT INTO " + CondroidDatabase.REMINDER_TABLE + " (pid, minutes) VALUES ('" + annotation.getPid() + "','" + i + "')");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Integer getReminder(int pid) {
        Cursor c = this.mDatabase.query(CondroidDatabase.REMINDER_TABLE, null, "pid = " + pid, null, null, null);
        if (c.getCount() > 0) {
            c.moveToNext();
            return Integer.parseInt(c.getString(c.getColumnIndex("minutes")));
        } else {
            return null;
        }
    }

    public boolean removeReminder(int pid) {
        try {
            this.mDatabase.query("DELETE FROM " + CondroidDatabase.REMINDER_TABLE + " WHERE pid=" + pid);
            return true;
        } catch (Exception e) {
            Log.w("Condroid", e);
            return false;
        }
    }


    public Reminder getNextReminder() {
        Cursor c = this.mDatabase.query("SELECT r.minutes AS remind, a.pid, a.* FROM " + CondroidDatabase.REMINDER_TABLE + " r JOIN " + CondroidDatabase.ANNOTATION_TABLE + " a USING (pid) ORDER by startTime ASC LIMIT 1");
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            Reminder r = new Reminder();
            r.annotation = this.readAnnotation(c);
            r.reminder = c.getInt(c.getColumnIndex("remind"));
            c.close();
            return r;
        }
        return null;
    }

    public List<Reminder> getReminderList() {
        List<Reminder> r = new ArrayList<Reminder>();

        Cursor c = this.mDatabase.query("SELECT r.minutes AS remind, a.pid, a.* FROM " + CondroidDatabase.REMINDER_TABLE + " r JOIN " + CondroidDatabase.ANNOTATION_TABLE + " a USING (pid) ORDER by startTime ASC LIMIT 20");
        if (c != null) {
            if (c.getCount() > 0)
                do {
                    Reminder reminder = new Reminder();
                    reminder.annotation = this.readAnnotation(c);
                    reminder.reminder = c.getInt(c.getColumnIndex("remind"));
                    r.add(reminder);
                } while (c.moveToNext());
            c.close();
        }
        return r;
    }

    public int getAnnotationsCount() {
        Cursor c = this.mDatabase.query("SELECT count(*) FROM annotations", null);
        int count = 0;
        if (c.getCount() > 0) {
            count = c.getInt(0);
        }
        c.close();
        return count;
    }
}
