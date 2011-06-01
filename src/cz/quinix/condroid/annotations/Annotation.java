package cz.quinix.condroid.annotations;

import java.io.Serializable;

public class Annotation implements Serializable {

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
	
	
}
