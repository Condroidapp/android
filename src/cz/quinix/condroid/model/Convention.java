package cz.quinix.condroid.model;

import android.content.ContentValues;
import cz.quinix.condroid.abstracts.DBInsertable;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.Date;

public class Convention implements Serializable, DBInsertable {
    /**
     *
     */
    private static final long serialVersionUID = -2754843728933013769L;
    private String name = "";
    private String iconUrl = "";
    private String date = "";
    private int cid = 0;
    private String dataUrl;
    private String message = "";
    private String locationsFile = "";
    private boolean providesTimetable = false;
    private boolean providesAnnotations = false;
    private Date lastUpdate = new Date();

    private static String dataURL = "http://condroid.fan-project.com/api/2/annotations/";


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message.trim();
    }

    public int getCid() {
        return cid;
    }

    public void setLastUpdate(Date lastUpdate1) {
        lastUpdate = lastUpdate1;
    }

    public String getName() {
        return name;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public void setDate(String date) {
        this.date = date.trim();
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl.trim();
    }

    public String getDataUrl() {
        return dataURL + this.cid;
    }

    public void setDataUrl(String url) {
        this.dataUrl = url;
    }

    public ContentValues getContentValues() {
        ContentValues ret = new ContentValues();
        ret.put("id", this.cid);
        ret.put("date", date);
        ret.put("iconUrl", iconUrl);
        ret.put("name", name);
        ret.put("dataUrl", dataUrl);
        ret.put("message", message);
        ret.put("locationsFile", locationsFile);
        ret.put("has_annotations", providesAnnotations);
        ret.put("has_timetable", providesTimetable);
        DateTimeFormatter dateSQLFormatter = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();
        ret.put("lastUpdate", dateSQLFormatter.print(lastUpdate.getTime()));

        return ret;
    }

    public void setLocationsFile(String file) {
        this.locationsFile = file;
    }

    public void setHasTimetable(boolean b) {
        this.providesTimetable = b;
    }

    public void setHasAnnotations(boolean b) {
        this.providesAnnotations = b;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }


}
