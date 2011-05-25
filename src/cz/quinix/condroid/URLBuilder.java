package cz.quinix.condroid;

import java.util.HashMap;
import java.util.Set;

public class URLBuilder {
	
	private HashMap<String, String> params;
	private String mainUrl = "";
	
	
	public URLBuilder(String mainUrl) {
		this.mainUrl = mainUrl;
		this.params = new HashMap<String, String>();
	}
	
	public void addParam(String paramName, String value) {
		this.params.put(paramName, value);
	}
	
	public String getUrl() {
		String uri = this.mainUrl;
		Set<String> keys = this.params.keySet();
		for (String param : keys) {
			if(uri.contains("?")) {
				uri += "&";
			} 
			else {
				uri += "?";
			}
			uri += param+"="+this.params.get(param);
		}
		return uri;		
	}

	public URLBuilder removeParam(String string) {
		this.params.remove(string);		
		return this;
	}

}
