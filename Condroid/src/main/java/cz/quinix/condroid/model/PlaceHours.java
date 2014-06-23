package cz.quinix.condroid.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by Jan on 21. 6. 2014.
 */
public class PlaceHours implements Serializable {

    private static final int TYPE_DAYS = 1;
    private static final int TYPE_WEEK = 2;

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    static SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
    static SimpleDateFormat dayFormat = new SimpleDateFormat("dd. MM. ");


    public int type;

    public Map<Integer, String[]> hours;

    private Map<Integer, Date[]> parsedDates;

    public int isOpen() {
        if (this.type == TYPE_DAYS) {
            return this.getCurrentDateHours() != null ? Place.STATE_OPEN : Place.STATE_CLOSED;
        } else if (this.type == TYPE_WEEK) {
            return this.getCurrentDayHours() != null ? Place.STATE_OPEN : Place.STATE_CLOSED;
        }

        return Place.STATE_UNKNOWN;
    }

    private Date[] getCurrentDayHours() {
        Date now = new Date();

        int dayOfWeek = now.getDay();
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }

        String[] currentDay = hours.get(dayOfWeek);

        if (currentDay == null) {
            return null;
        }

        String[] openHour = currentDay[0].split(":");
        String[] closeHour = currentDay[1].split(":");

        Date opening = new Date();
        opening.setHours(Integer.parseInt(openHour[0]));
        opening.setMinutes(Integer.parseInt(openHour[1]));

        Date closing = new Date();
        closing.setHours(Integer.parseInt(closeHour[0]));
        closing.setMinutes(Integer.parseInt(closeHour[1]));

        if (closing.before(opening)) {
            closing.setDate(closing.getDate() + 1);
        }
        if (now.after(opening) && now.before(closing)) {
            return new Date[]{opening, closing};
        }
        return null;
    }

    private Date[] getCurrentDateHours() {
        Date now = new Date();
        parseData();

        for (int key : parsedDates.keySet()) {
            Date[] hours = parsedDates.get(key);

            Date opening = hours[0];
            Date closing = hours[1];
            if (now.before(opening)) {
                return null;
            }

            if (now.after(opening) && now.before(closing)) {
                return hours;
            }
        }
        return null;
    }

    private void parseData() {
        if (parsedDates == null) {
            parsedDates = new HashMap<Integer, Date[]>();

            for (int key : hours.keySet()) {
                String[] dates = hours.get(key);

                try {
                    Date opening = dateFormat.parse(dates[0]);
                    Date closing = dateFormat.parse(dates[1]);
                    parsedDates.put(key, new Date[]{opening, closing});
                } catch (ParseException e) {

                }
            }
        }
    }

    public String getReadableTitleFor(int key) {
        if (this.type == TYPE_DAYS) {
            String[] values = hours.get(key);

            try {
                Date opening = dateFormat.parse(values[0]);
                return dayFormat.format(opening);
            } catch (ParseException e) {
                return "";
            }
        } else {
            String[] dayOfWeek = {"", "po", "út", "st", "čt", "pá", "so", "ne"};

            return dayOfWeek[key];
        }
    }

    public String[] getHoursFor(int key) {
        if (this.type == TYPE_DAYS) {
            String[] values = hours.get(key);

            try {
                Date opening = dateFormat.parse(values[0]);
                Date closing = dateFormat.parse(values[1]);
                return new String[]{hourFormat.format(opening), hourFormat.format(closing)};
            } catch (ParseException e) {
                return null;
            }
        } else {
            return hours.get(key);
        }

    }

    public boolean isToday(int key) {
        if (type == TYPE_WEEK) {
            Date now = new Date();

            int dayOfWeek = now.getDay();
            if (dayOfWeek == 0) {
                dayOfWeek = 7;
            }
            return dayOfWeek == key;
        } else {
            parseData();
            Date[] hours = parsedDates.get(key);
            Calendar today = Calendar.getInstance(TimeZone.getDefault(), new Locale("cs", "CZ"));
            today.setTime(new Date());

            Calendar compared = Calendar.getInstance();
            compared.setTime(hours[0]);

            //its today
            return compared.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                    && compared.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                    && compared.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
        }
    }

    public Integer[] getKeys() {
        if (type == TYPE_WEEK) {
            return new Integer[]{1, 2, 3, 4, 5, 6, 7};
        } else {
            Set<Integer> i = hours.keySet();

            return i.toArray(new Integer[i.size()]);
        }
    }
}
