package cz.quinix.condroid;

import java.util.HashMap;
import java.util.Map;

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

	

}
