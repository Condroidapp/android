package cz.quinix.condroid.model;

import java.io.Serializable;
import java.util.Date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import android.content.ContentValues;
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
	private String additonalTypes;
	private String programLine;
	private String annotation ="";
	private Date startTime;
	private Date endTime;
	private String location;
	static DateTimeFormatter dateISOFormatter;
	static DateTimeFormatter dateSQLFormatter;
	private int lid;
	static {
		dateISOFormatter = ISODateTimeFormat.dateTimeNoMillis();
		dateSQLFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();
		
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = parseDate(startTime,dateISOFormatter);
	}

	public void setEndTime(String endTime) {
			this.endTime = parseDate(endTime,dateISOFormatter);
	}
	
	public void setSQLStartTime(String startTime) {
		this.startTime = parseDate(startTime,dateSQLFormatter);
	}

	public void setSQLEndTime(String endTime) {
			this.endTime = parseDate(endTime,dateSQLFormatter);
	}
	
	private Date parseDate(String date, DateTimeFormatter formatter) {
		if (date == null || date.equals(""))
			return null;
		return formatter.parseDateTime(date).toDate();

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

	/**
	 * Use only during processing new XML!
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
		this.length = length.trim();
	}

	public void setType(String type) {
		String[] types = type.trim().split("+");
		if(types.length > 0) {
			mainType = types[0];
		}
		if(types.length > 1) {
			for(int i=1; i<types.length; i++) {
				additonalTypes+=types[i]+"+";
			}
			additonalTypes.subSequence(0, additonalTypes.length()-1); //removes last +
		}
		
	}

	public void setProgramLine(String programLine) {
		this.programLine = programLine.trim();
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation.trim();
	}

	public ContentValues getContentValues() {
		
		if(startTime != null && endTime != null && startTime.after(endTime)) {
			endTime.setDate(endTime.getDate()+1);
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
		if(startTime != null) {
			ret.put("startTime", dateSQLFormatter.print(startTime.getTime()));
		}
		if(endTime != null) {
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
	
	

}
