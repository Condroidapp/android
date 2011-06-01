package cz.quinix.condroid.conventions;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Convention implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2754843728933013769L;
	private String name = "";
	private String iconUrl = "";
	private String date = "";
	private int cid = 0;
	
	public int getCid() {
		return cid;
	}
	public String getDate() {
		return date;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public String getName() {
		return name;
	}
	
	public void setCid(int cid) {
		this.cid = cid;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	
	
	
	public Bitmap getImage() {
		Bitmap bitmap = null;
		if(iconUrl != null) {
			InputStream io;
			try {
				io = (InputStream) new URL(this.iconUrl).getContent();
				bitmap = BitmapFactory.decodeStream(io);
				
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return bitmap;
	}	
}
