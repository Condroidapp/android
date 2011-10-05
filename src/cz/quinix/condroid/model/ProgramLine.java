package cz.quinix.condroid.model;

import cz.quinix.condroid.abstracts.ICondition;

public class ProgramLine implements ICondition {
	private int lid;
	private String name;
	
	public int getLid() {
		return lid;
	}
	public String getName() {
		return name;
	}
	public void setLid(int lid) {
		this.lid = lid;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCondition() {
		return "lid = "+lid;
	}
	
	
}
