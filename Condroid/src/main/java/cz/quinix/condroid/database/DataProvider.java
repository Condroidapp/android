package cz.quinix.condroid.database;

import android.database.Cursor;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.model.Place;
import cz.quinix.condroid.model.ProgramLine;
import cz.quinix.condroid.model.Reminder;
import cz.quinix.condroid.util.DateTimeFactory;

@Singleton
public class DataProvider {

	public static int ITEMS_PER_PAGE = 40;

	@Inject
	private CondroidDatabase mDatabase;

	private Convention con;

	private List<Integer> favorited;

	private Map<Integer, ProgramLine> programLines = null;

	private List<Place> places;

	public boolean hasData() {
		return !mDatabase.isEmpty();
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
		if (programLines == null) {
			this.loadProgramLines();
		}
		if (programLines != null) {
			if (programLines.containsKey(lid)) {
				return programLines.get(lid);
			}
		}

		return new ProgramLine();
	}

	public Map<Integer, ProgramLine> getProgramLines() {
		if (programLines == null) {
			this.loadProgramLines();
		}
		return programLines;
	}

	private void loadProgramLines() {
		programLines = new HashMap<Integer, ProgramLine>();

		Cursor c = this.mDatabase.query(CondroidDatabase.LINE_TABLE, null, null, null, "title ASC", null);
		while (c.moveToNext()) {
			ProgramLine p = new ProgramLine();
			p.setLid(c.getInt(c.getColumnIndex("id")));
			p.setName(c.getString(c.getColumnIndex("title")));
			programLines.put(p.getLid(), p);
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

		String now = DateTimeFactory.getNow().toDateTime(DateTimeZone.forID("UTC")).toString("yyyy-MM-dd HH:mm");
		if (skip == 0) {
			Cursor c = this.mDatabase.query(CondroidDatabase.ANNOTATION_TABLE, null, "startTime < '"+ now +"' AND endTime > '"+ now +"'" + (condition != null && !condition.equals("") ? " AND " + condition : ""), null, "startTime DESC, lid ASC, pid ASC", null, false);
			while (c.moveToNext()) {
				Annotation annotation = readAnnotation(c);

				l.add(annotation);

			}
			skip = c.getCount();
			c.close();
		}

		if (l.size() < ITEMS_PER_PAGE) {
			Cursor c2 = this.mDatabase.query(
					CondroidDatabase.ANNOTATION_TABLE,
					null,
					"(startTime < '" + now + "' AND endTime > '" + now + "') OR (startTime >'" + now + "')" + (condition != null && !condition.equals("") ? " AND " + condition : ""),
					null,
					"startTime ASC, lid ASC, pid ASC",
					(skip) + "," + (ITEMS_PER_PAGE - l.size()),
					false
			);
			Log.d("Condroid", "LIMIT: " + ((skip) + "," + (ITEMS_PER_PAGE - l.size())) + ", returned items " + c2.getCount());

			while (c2.moveToNext()) {
				Annotation annotation = readAnnotation(c2);
				l.add(annotation);
			}
			c2.close();
		}

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
		annotation.setImdb(c.getString(c.getColumnIndex("imdb")));
		annotation.setLid(c.getInt(c.getColumnIndex("lid")));
		annotation.setSQLStartTime(c.getString(c.getColumnIndex("startTime")));
		annotation.setType(c.getString(c.getColumnIndex("mainType")), c.getString(c.getColumnIndex("additionalTypes")));
		return annotation;
	}

	public Convention getCon() {
		if (this.con != null) {
			return con;
		}
		Cursor c = this.mDatabase.query(CondroidDatabase.CON_TABLE, null, null, null, null, null);
		Convention co = new Convention();
		while (c.moveToNext()) {
			co.setId(c.getInt(c.getColumnIndex("id")));
			co.setDate(c.getString(c.getColumnIndex("date")));
			co.setImage(c.getString(c.getColumnIndex("image")));
			co.setName(c.getString(c.getColumnIndex("name")));
			co.setUrl(c.getString(c.getColumnIndex("url")));
			co.setMessage(c.getString(c.getColumnIndex("message")));
			co.setGps(c.getString(c.getColumnIndex("gps")));
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

	public CondroidDatabase getDatabase() {
		return mDatabase;
	}

	public void clear() {
		this.programLines = null;
		this.con = null;
		this.favorited = null;
		this.places = null;
	}

	public List<Place> getPlaces() {
		if (this.places != null) {
			return this.places;
		}
		List<Place> ret = new ArrayList<Place>();

		Cursor c = this.mDatabase.query(CondroidDatabase.PLACES_TABLE, null, null, null, "category_sort ASC, sort ASC, name ASC", null);

		while (c.moveToNext()) {
			Place place = new Place();
			place.setId(c.getInt(c.getColumnIndex("id")));
			place.setName(c.getString(c.getColumnIndex("name")));
			place.setUrl(c.getString(c.getColumnIndex("url")));
			place.setCategory(c.getString(c.getColumnIndex("category")));
			place.setAddress(c.getString(c.getColumnIndex("address")));
			place.setCategorySort(c.getInt(c.getColumnIndex("category_sort")));
			place.setDescription(c.getString(c.getColumnIndex("description")));
			place.setHours(c.getString(c.getColumnIndex("hours")));
			place.setGps(c.getString(c.getColumnIndex("gps")));

			ret.add(place);
		}
		c.close();
		return this.places = ret;
	}

}
