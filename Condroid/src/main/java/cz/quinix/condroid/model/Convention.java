package cz.quinix.condroid.model;

import android.content.ContentValues;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import cz.quinix.condroid.abstracts.DBInsertable;

public class Convention implements Serializable, DBInsertable {
    /**
     *
     */
    private static final long serialVersionUID = -2754843728933013769L;
    private String name = "";
    private String image = "";
    private String date = "";
    private List<Place> places;
    private Gps gps;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url = "";

    private Date start;
    private Date end;
    private int id = 0;
    private String message = "";
    private boolean timetable = false;
    private boolean annotations = false;
    private Date lastUpdate = new Date();


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (message != null) {
            this.message = message.trim();
        }
    }

    public int getId() {
        return id;
    }

    public void setLastUpdate(Date lastUpdate1) {
        lastUpdate = lastUpdate1;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date.trim();
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public void setImage(String image) {
        if (image != null) {
            this.image = image.trim();
        }
    }


    public ContentValues getContentValues() {
        ContentValues ret = new ContentValues();
        ret.put("id", this.id);
        ret.put("date", date);
        ret.put("image", image);
        ret.put("name", name);
        ret.put("url", url);
        ret.put("message", message);
        ret.put("has_annotations", annotations);
        ret.put("has_timetable", timetable);
        ret.put("gps", gps != null ? gps.lat + ";" + gps.lon : null);
        DateTimeFormatter dateSQLFormatter = DateTimeFormat
                .forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();
        ret.put("lastUpdate", dateSQLFormatter.print(lastUpdate.getTime()));

        return ret;
    }

    public void setHasTimetable(boolean b) {
        this.timetable = b;
    }

    public void setHasAnnotations(boolean b) {
        this.annotations = b;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public String getDate() {
        return date;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setPlaces(List<Place> o) {
        this.places = o;
    }

    public List<Place> getPlaces() {
        return places;
    }


    public void setGps(String string) {
        if (string == null) {
            return;
        }

        String[] x = string.split(";");
        if (x.length > 0) {
            this.gps = new Gps();
            gps.lat = Float.parseFloat(x[0]);
            gps.lon = Float.parseFloat(x[1]);
        }
    }

    public Gps getGps() {
        return gps;
    }
}
