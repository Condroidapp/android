package cz.quinix.condroid.model;

import android.content.ContentValues;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import cz.quinix.condroid.abstracts.DBInsertable;

/**
 * Created by Jan on 21. 6. 2014.
 */
public class Place implements Serializable, DBInsertable {

    public static final int STATE_OPEN = 1;
    public static final int STATE_CLOSED = 0;
    public static final int STATE_UNKNOWN = -1;

    private int id;
    private String name;
    private String description;

    private PlaceHours hours;


    private int sort;
    private String category;
    private int categorySort;

    private Gps gps;
    private String[] address;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlaceHours getHours() {
        return hours;
    }

    public void setHours(PlaceHours hours) {
        this.hours = hours;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCategorySort() {
        return categorySort;
    }

    public void setCategorySort(int categorySort) {
        this.categorySort = categorySort;
    }

    public Gps getGps() {
        return gps;
    }

    public void setGps(Gps gps) {
        this.gps = gps;
    }

    public String[] getAddress() {
        return address;
    }

    public void setAddress(String[] address) {
        this.address = address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues c = new ContentValues();

        c.put("name", name);
        c.put("description", description);
        c.put("hours", new Gson().toJson(hours));
        c.put("sort", sort);
        c.put("category", category);
        c.put("category_sort", categorySort);
        c.put("gps", gps != null ? gps.lat + ";" + gps.lon : null);
        c.put("address", StringUtils.join(address, ";"));
        c.put("id", id);
        c.put("url", url);

        return c;
    }

    public void setAddress(String string) {
        this.address = string.split(";");
    }

    public void setHours(String hours) {
        this.hours = new Gson().fromJson(hours, PlaceHours.class);
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

    public int isOpen() {
        if (hours == null) {
            return STATE_UNKNOWN;
        }

        return hours.isOpen();
    }
}
