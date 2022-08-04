package webserver.utils;

import static exception.ExceptionStrings.INVALID_QUERY_STRING;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QueryStrings {

    private static final String QUERY_STRING_SPLIT_REGEX = "=";
    private static final String QUERIES_SPLIT_REGEX = "&";
    private static final String EMPTY_VALUE = "";
    private final Map<String, String> map;

    private QueryStrings(Map<String, String> queryStringMap) {
        this.map = queryStringMap;
    }

    public static QueryStrings of(String queryStrings) {
        Objects.requireNonNull(queryStrings);

        if (queryStrings.isEmpty()) {
            return new QueryStrings(Collections.emptyMap());
        }
        return new QueryStrings(parseQueryStrings(queryStrings));
    }

    public static QueryStrings empty() {
        return new QueryStrings(Maps.newHashMap());
    }

    public static Map<String, String> toMap(String queryStrings) {
        Objects.requireNonNull(queryStrings);

        if (queryStrings.isEmpty()) {
            return Collections.emptyMap();
        }
        return parseQueryStrings(queryStrings);
    }

    private static Map<String, String> parseQueryStrings(String queries) {
        String[] queryStrings = queries.split(QUERIES_SPLIT_REGEX);
        Map<String, String> queryStringsMap = new HashMap<>();
        for (String string : queryStrings) {
            String[] queryString = string.split(QUERY_STRING_SPLIT_REGEX);
            validate(queryString);
            queryStringsMap.put(queryString[0], queryString[1]);
        }

        return queryStringsMap;
    }

    private static void validate(String[] queryString) {
        if (queryString.length < 2) {
            throw new IllegalArgumentException(INVALID_QUERY_STRING);
        }
    }

    public Map<String, String> map() {
        return map;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public int size() {
        return map.size();
    }

    public String get(String key) {
        return map.getOrDefault(key, EMPTY_VALUE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueryStrings that = (QueryStrings) o;
        return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }
}