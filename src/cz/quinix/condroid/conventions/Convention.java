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
	public String name = "";
	public String iconUrl = "";
	public String date = "";
	public int cid = 0;
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
