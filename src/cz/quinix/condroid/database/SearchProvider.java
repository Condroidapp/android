package cz.quinix.condroid.database;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 24.4.12
 * Time: 22:08
 * To change this template use File | Settings | File Templates.
 */
public class SearchProvider {

    private static Map<String, SearchQueryBuilder> map;

    static {
        map = new HashMap<String, SearchQueryBuilder>();
    }


    public static SearchQueryBuilder getSearchQueryBuilder(String tag) {
        if (!map.containsKey(tag)) {
            map.put(tag, new SearchQueryBuilder());
        }
        return map.get(tag);
    }

    public static Map<String, SearchQueryBuilder> getSearchQueryBuilders() {
        return map;
    }
}
