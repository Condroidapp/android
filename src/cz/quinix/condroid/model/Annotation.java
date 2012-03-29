package cz.quinix.condroid.model;

import java.io.Serializable;
import java.util.Date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import android.content.ContentValues;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.DBInsertable;

public class Annotation implements Serializable, DBInsertable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 29890241539328629L;

	private String pid;
	private String talker;
	private String title;
	private String length;
	private String mainType;
	private String additonalTypes = "";
	private String programLine;
	private String annotation = "";
	private Date startTime;
	private Date endTime;
	private String location;
	public static DateTimeFormatter dateISOFormatter = ISODateTimeFormat
			.dateTimeNoMillis();
	public static DateTimeFormatter lameISOFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd'T'HH:mmZZ");
	public static DateTimeFormatter dateSQLFormatter = DateTimeFormat
			.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();
	private int lid;

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = parseDate(startTime, dateISOFormatter);
	}

	public void setEndTime(String endTime) {
		this.endTime = parseDate(endTime, dateISOFormatter);
	}

	public void setSQLStartTime(String startTime) {
		this.startTime = parseDate(startTime, dateSQLFormatter);
	}

	public void setSQLEndTime(String endTime) {
		this.endTime = parseDate(endTime, dateSQLFormatter);
	}

	private Date parseDate(String date, DateTimeFormatter formatter) {
		date = date.trim();
		if (date == null || date.equals(""))
			return null;
		Date x = null;
		try {
			x = formatter.parseDateTime(date).toDate();
		} catch (IllegalArgumentException e) {
			if (formatter.equals(dateISOFormatter))
				x = lameISOFormatter.parseDateTime(date).toDate();
			else
				throw e;
		}
		return x;

	}

	public void setAdditonalTypes(String additonalTypes) {
		this.additonalTypes = additonalTypes;
	}

	public String getPid() {
		return pid;
	}

	public String getAuthor() {
		return talker;
	}

	public String getTitle() {
		return title;
	}

	public String getLength() {
		return length;
	}

	public String getType() {
		return mainType;
	}

	public int getProgramIcon() {
		String type = this.mainType;
		if (type.equalsIgnoreCase("P")) {
			return R.drawable.lecture;
		}
		if (type.equalsIgnoreCase("B")) {
			return R.drawable.discussion;
		}
		if (type.equalsIgnoreCase("C")) {
			return R.drawable.theatre;
		}
		if (type.equalsIgnoreCase("D")) {
			return R.drawable.projection;
		}
		if (type.equalsIgnoreCase("F")) {
			return R.drawable.projection;
		}
		if (type.equalsIgnoreCase("G")) {
			return R.drawable.game;
		}
		if (type.equalsIgnoreCase("H")) {
			return R.drawable.music;
		}
		if (type.equalsIgnoreCase("Q")) {
			return R.drawable.game;
		}
		if (type.equalsIgnoreCase("W")) {
			return R.drawable.workshop;
		}
		return R.drawable.program_unknown;

	}

	/**
	 * Use only during processing new XML!
	 * 
	 * @return
	 */
	public String getProgramLine() {
		return programLine;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setPid(String pid) {
		this.pid = pid.trim();
	}

	public void setAuthor(String talker) {
		this.talker = talker.trim();
	}

	public void setTitle(String title) {
		this.title = title.trim();
	}

	public void setLength(String length) {
		// this.length = length.trim();
	}

	public void setType(String type) {
		String[] types = type.trim().split("\\+");
		if (types.length > 0) {
			mainType = types[0].trim();
		}
		if (types.length > 1) {
			for (int i = 1; i < types.length; i++) {
				additonalTypes += types[i] + "+";
			}
			additonalTypes = (String) additonalTypes.subSequence(0,
					additonalTypes.length() - 1); // removes last +
		}

	}

	public void setProgramLine(String programLine) {
		this.programLine = programLine.trim();
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation.trim();
	}

	public ContentValues getContentValues() {

		if (startTime != null && endTime != null && startTime.after(endTime)) {
			endTime.setDate(endTime.getDate() + 1);
		}
		ContentValues ret = new ContentValues();
		ret.put("pid", this.pid);
		ret.put("talker", talker);
		ret.put("title", title);
		ret.put("length", length);
		ret.put("mainType", mainType);
		ret.put("additionalTypes", additonalTypes);

		ret.put("lid", lid);

		ret.put("location", location);
		ret.put("annotation", annotation);
		if (startTime != null) {
			ret.put("startTime", dateSQLFormatter.print(startTime.getTime()));
		}
		if (endTime != null) {
			ret.put("endTime", dateSQLFormatter.print(endTime.getTime()));
		}

		return ret;
	}

	public void setLid(Integer integer) {
		lid = integer.intValue();

	}

	public int getLid() {
		return lid;
	}

	public void setLocation(String nextText) {
		location = nextText;
	}

	public String getLocation() {
		return location;
	}

	public String[] getAdditionalTypes() {
		return additonalTypes.split("\\+");
	}
}
