package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiwei
 * @since 2013-7-14
 */
public class EasyMap<K, V> extends HashMap<K, V> {
    public EasyMap() {
    }

    public EasyMap(K k, V v) {
        easyPut(k, v);
    }

    public EasyMap<K, V> easyPut(K k, V v) {
        super.put(k, v);
        return this;
    }

    public EasyMap<K, V> notNullPut(K k, V v) {
        if (v == null) return this;
        super.put(k, v);
        return this;
    }

    public EasyMap<K, V> easyRemove(V name) {
        super.remove(name);
        return this;
    }

    public EasyMap<K, V> easyPutAll(Map<? extends K, ? extends V> map) {
        super.putAll(map);
        return this;
    }
}
