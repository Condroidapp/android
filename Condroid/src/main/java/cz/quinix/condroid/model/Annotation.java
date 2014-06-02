package cz.quinix.condroid.model;

import android.content.ContentValues;
import android.text.TextUtils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.DBInsertable;

public class Annotation implements Serializable, DBInsertable {

    /**
     *
     */
    private static final long serialVersionUID = 29890241539328629L;

    private int pid;
    private String author;
    private String title;
    private AnnotationType type = new AnnotationType();
    private String programLine;
    private String annotation = "";
    private Date start;
    private Date end;
    private String location;
    public static DateTimeFormatter dateISOFormatter = ISODateTimeFormat
            .dateTimeNoMillis();
    public static DateTimeFormatter dateSQLFormatter = DateTimeFormat
            .forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();
    private int lid;


    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public void setStart(String start) {
        this.start = parseDate(start, dateISOFormatter);
    }

    public void setEnd(String end) {
        this.end = parseDate(end, dateISOFormatter);
    }

    public void setSQLStartTime(String startTime) {
        this.start = parseDate(startTime, dateSQLFormatter);
    }

    public void setSQLEndTime(String endTime) {
        this.end = parseDate(endTime, dateSQLFormatter);
    }

    private Date parseDate(String date, DateTimeFormatter formatter) {

        if (date == null || date.equals(""))
            return null;
        date = date.trim();
        Date x;
        try {
            if (date.length() < 25 && formatter.equals(dateISOFormatter)) {
                date = date.substring(0, date.length() - 6) + ":00" + date.substring(date.length() - 6);
            }
            x = formatter.parseDateTime(date).toDate();
        } catch (IllegalArgumentException e) {
            //if (formatter.equals(dateISOFormatter))
            //x = lameISOFormatter.parseDateTime(date).toDate();
            //else
            throw e;
        }
        return x;

    }


    public int getPid() {
        return pid;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public AnnotationType getType() {
        return this.type;
    }


    public int getProgramIcon() {
        String type = this.type.mainType;
        if (type.equalsIgnoreCase("P")) {
            return R.drawable.lecture;
        }
        if (type.equalsIgnoreCase("B")) {
            return R.drawable.discussion;
        }
        if (type.equalsIgnoreCase("C")) {
            return R.drawable.theatre;
        }
        if (type.equalsIgnoreCase("D")) {
            return R.drawable.projection;
        }
        if (type.equalsIgnoreCase("F")) {
            return R.drawable.projection;
        }
        if (type.equalsIgnoreCase("G")) {
            return R.drawable.game;
        }
        if (type.equalsIgnoreCase("H")) {
            return R.drawable.music;
        }
        if (type.equalsIgnoreCase("Q")) {
            return R.drawable.game;
        }
        if (type.equalsIgnoreCase("W")) {
            return R.drawable.workshop;
        }
        return R.drawable.program_unknown;

    }

    /**
     * Use only during processing new XML!
     *
     * @return
     */
    public String getProgramLine() {
        return programLine;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setPid(String pid) {
        try {
            this.pid = Integer.parseInt(pid.trim());
        } catch (NumberFormatException e) {
            //intentionally
        }
    }

    public void setAuthor(String talker) {
        if (talker != null) {
            this.author = talker.trim();
        }
    }

    public void setTitle(String title) {
        this.title = title.trim();
    }

    public void setType(AnnotationType type) {
        this.type = type;
    }

    public void setProgramLine(String programLine) {
        this.programLine = programLine.trim();
    }

    public void setAnnotation(String annotation) {
        if (annotation != null) {
            this.annotation = annotation.trim();
        }
    }

    public ContentValues getContentValues() {

        if (start != null && end != null && start.after(end)) {
            end.setDate(end.getDate() + 1);
        }
        ContentValues ret = new ContentValues();
        ret.put("pid", this.pid);
        ret.put("talker", author);
        ret.put("title", title);
        ret.put("mainType", this.type.mainType);
        ret.put("additionalTypes", TextUtils.join("+", this.type.secondaryTypes));
        ret.put("normalizedTitle", normalize(title));

        ret.put("lid", lid);

        ret.put("location", location);
        ret.put("annotation", annotation);
        if (start != null) {
            ret.put("startTime", dateSQLFormatter.print(start.getTime()));
        }
        if (end != null) {
            ret.put("endTime", dateSQLFormatter.print(end.getTime()));
        }

        return ret;
    }


    public void setLid(Integer integer) {
        lid = integer;

    }

    public int getLid() {
        return lid;
    }

    public void setLocation(String nextText) {
        location = nextText;
    }

    public String getLocation() {
        return location;
    }

    public String[] getAdditionalTypes() {
        return (String[]) type.secondaryTypes.toArray();
    }

    public void setStartTime(Date startTime) {
        this.start = startTime;
    }

    private static final HashMap<Character, Character> accents = new HashMap<Character, Character>() {
        {
            put('ě', 'e');
            put('š', 's');
            put('č', 'c');
            put('ř', 'r');
            put('ž', 'z');
            put('ý', 'y');
            put('á', 'a');
            put('í', 'i');
            put('é', 'e');

            put('ú', 'u');
            put('ů', 'u');
            put('ľ', 'l');
            put('ŕ', 'r');
            put('ť', 't');
            put('ü', 'u');
            put('ä', 'a');
            put('ï', 'i');
            put('ë', 'e');
            put('ó', 'o');
            put('ś', 's');
            put('ď', 'd');
            put('ĺ', 'l');
            put('ź', 'z');
            put('ć', 'c');
            put('ň', 'n');
        }
    };

    public static String normalize(String title) {
        char[] normalized = title.toLowerCase().toCharArray();
        for (int i = 0; i < normalized.length; i++) {
            Character x = accents.get(normalized[i]);
            if (x != null)
                normalized[i] = x;
        }
        return new String(normalized);
    }

    public void setType(String mainType, String additionalTypes) {
        this.type = new AnnotationType();
        this.type.mainType = mainType;
        this.type.secondaryTypes = Arrays.asList(additionalTypes.split("\\+"));
    }
}
