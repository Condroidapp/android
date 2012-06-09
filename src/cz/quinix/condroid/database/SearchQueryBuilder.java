package cz.quinix.condroid.database;

import android.util.Log;
import cz.quinix.condroid.abstracts.ICondition;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SearchQueryBuilder {

    private Map<String, ICondition> params;


    public SearchQueryBuilder() {
        this.params = new HashMap<String, ICondition>();
    }

    public SearchQueryBuilder addParam(final String value) {
        if (value != null) {
            this.params.put(new String().getClass().getName(), new ICondition() {
                @Override
                public String getCondition() {
                    return "pid LIKE '%" + value + "%' OR title LIKE '%" + value + "%' OR talker LIKE '%" + value + "%'";
                }

                @Override
                public String getReadable() {
                    return "*" + value + "*";  //To change body of implemented methods use File | Settings | File Templates.
                }
            });
        }

        return this;
    }

    public SearchQueryBuilder addParam(ICondition value) {
        this.params.put(value.getClass().getName(), value);
        return this;
    }

    public SearchQueryBuilder addParam(ICondition value, String key) {
        this.params.put(key, value);
        return this;
    }

    public SearchQueryBuilder removeParam(Object value) {
        this.params.remove(value.getClass().getName());
        return this;
    }

    public boolean hasParam(Object value) {
        return this.params.containsKey(value.getClass().getName());

    }

    public void addParam(final Date d) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        this.addParam(new ICondition() {

            public String getCondition() {
                Date d2 = (Date) d.clone();
                d2.setDate(d.getDate() + 1);
                return "(startTime >=DATE('" + df.format(d) + "') AND startTime < DATE('" + df.format(d2) + "'))";
            }

            @Override
            public String getReadable() {
                return new SimpleDateFormat("dd.MM.yy").format(d);
            }
        }, new Date().getClass().getName());
    }

    public String buildCondition() {
        String condition = "";
        for (String key : params.keySet()) {
            if (!condition.equals("")) {
                condition += " AND ";
            }
            String cd = params.get(key).getCondition();
            if (!cd.trim().equals("")) {
                condition += " (" + cd + ")";
            }
        }
        Log.d("Condroid", "Builded URL " + condition);
        return condition;
    }

    public void clear() {
        params.clear();
    }

    public boolean isEmpty() {
        return params.isEmpty();
    }


    public String getReadableCondition() {
        String ret = "Filtr: ";
        for (String key : params.keySet()) {
            if (!ret.equals("Filtr: ")) {
                ret += " & ";
            }
            ret += params.get(key).getReadable();
        }
        return ret;
    }
}
