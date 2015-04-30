package cz.quinix.condroid.database;

import java.util.HashMap;
import java.util.Map;

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

	public static void clear() {

		for (SearchQueryBuilder sb : map.values()) {
			sb.clear();
		}

		map = new HashMap<String, SearchQueryBuilder>();
	}
}
