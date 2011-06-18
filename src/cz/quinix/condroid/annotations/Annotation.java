package cz.quinix.condroid.annotations;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import cz.quinix.condroid.DBInsertable;

public class Annotation implements Serializable, DBInsertable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 29890241539328629L;

	private String pid;
	private String talker;
	private String title;
	private String length;
	private String type;
	private String programLine;
	private String annotation;
	private Date startTime;
	private Date endTime;

	private int lid;
	static DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm");

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = parseDate(startTime);
	}

	public void setEndTime(String endTime) {
			this.endTime = parseDate(endTime);
	}
	private Date parseDate(String date) {
		if (date == null || date.equals(""))
			return null;
		try {
			return df.parse(date);
		} catch (ParseException e) {
			return null;
		}
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
		return type;
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
		this.type = type.trim();
	}

	public void setProgramLine(String programLine) {
		this.programLine = programLine.trim();
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation.trim();
	}

	public ContentValues getContentValues() {
		ContentValues ret = new ContentValues();
		ret.put("pid", this.pid);
		ret.put("talker", talker);
		ret.put("title", title);
		ret.put("length", length);
		ret.put("type", type);
		ret.put("lid", lid);
		ret.put("annotation", annotation);
		if(startTime != null) {
			ret.put("startTime", df.format(startTime));
		}
		if(endTime != null) {
			ret.put("endTime", df.format(endTime));
		}
		
		return ret;
	}

	public void setLid(Integer integer) {
		lid = integer.intValue();
		
	}

	public int getLid() {
		return lid;
	}
	
	

}
