package cz.quinix.condroid.database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.quinix.condroid.abstracts.ICondition;

import android.util.Log;

public class SearchQueryBuilder {
	
	private Map<String, String> params;
	
	
	public SearchQueryBuilder() {
		this.params = new HashMap<String, String>();
	}
	
	public SearchQueryBuilder addParam(String value) {
		if(value != null) {
			this.params.put("stub", "pid LIKE '%"+value+"%' OR title LIKE '%"+value+"%'");
		}
		
		return this;
	}
	
	public SearchQueryBuilder addParam (ICondition value) {
		this.params.put(value.getClass().getName(), value.getCondition());
		return this;
	}
	public SearchQueryBuilder addParam (ICondition value, String key) {
		this.params.put(key, value.getCondition());
		return this;
	}
	
	public SearchQueryBuilder removeParam (Object value) {
		this.params.remove(value.getClass().getName());
		return this;
	}
	
	public boolean hasParam (Object value) {
		return this.params.containsKey(value.getClass().getName());
		
	}
	
	public void addParam(Date d) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final String date = df.format(d);
		d.setDate(d.getDate()+1);
		final String date2 = df.format(d);
		this.addParam(new ICondition() {
			

			
			public String getCondition() {
				return "(startTime >=DATE('"+ date + "') AND startTime < DATE('"+ date2 +"'))";
			}
		},"java.util.Date");
	}
	
	public String buildCondition() {
		String condition = "";
		for (String key : params.keySet()) {
			if(!condition.equals("")) {
				condition += " AND ";
			}
			condition += " ("+params.get(key)+")";
		}
		Log.i("Condroid URL", "Builded URL "+ condition);
		return condition;
	}

	public void clear() {
		params.clear();
	}

	public boolean isEmpty() {
		return params.isEmpty();
	}

	

	

}
