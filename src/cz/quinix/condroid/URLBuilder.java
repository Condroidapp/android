package cz.quinix.condroid;

import java.util.HashMap;
import java.util.Set;

import android.util.Log;

public class URLBuilder {
	
	private HashMap<String, String> params;
	private String mainUrl = "";
	
	
	public URLBuilder(String mainUrl) {
		this.mainUrl = mainUrl;
		this.params = new HashMap<String, String>();
	}
	
	public URLBuilder addParam(String paramName, String value) {
		this.params.put(paramName, value);
		return this;
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
		Log.i("Condroid URL", "Builded URL "+ uri);
		return uri;		
	}

	public URLBuilder removeParam(String string) {
		this.params.remove(string);		
		return this;
	}

}
