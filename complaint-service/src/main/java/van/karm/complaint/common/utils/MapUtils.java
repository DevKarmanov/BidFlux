package van.karm.complaint.common.utils;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public final class MapUtils {
    private MapUtils() {}

    @SafeVarargs
    public static <K, V> Map<K, V> ofNonNull(Map.Entry<K, V>... entries) {
        Map<K, V> map = new HashMap<>();
        for (Map.Entry<K, V> entry : entries) {
            if (entry != null && entry.getValue() != null) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }
}

