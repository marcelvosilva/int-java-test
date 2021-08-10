package br.com.inter.testejava.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

@Component
@Configurable
public class Cache<K, V> {
    public LinkedHashMap<K, V> cacheMap;

    protected class cacheObject {
        public V value;

        protected cacheObject(V value) {
            this.value = value;
        }
    }

    public Cache() {
        cacheMap = new LinkedHashMap<K, V>(10) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
                return size() > 10;
            }
        };
    }

    public void put(K key, V value) {
        synchronized (cacheMap) {
            cacheMap.put(key, value);
        }
    }

    public Object get(K key) {
        synchronized (cacheMap) {
            cacheObject c = new cacheObject(cacheMap.get(key));
            if (c.value == null)
                return null;
            else {
                return c.value.toString();
            }
        }
    }
    
}
