package org.wildfly.extension.microprofile.config;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A registry whose items can be iterated over.
 * @author Paul Ferraro
 */
public class IterableRegistry<T> implements Iterable<T>, Registry<T> {

    private final Map<String, T> objects = new ConcurrentHashMap<>();

    @Override
    public void register(String name, T object) {
        this.objects.put(name, object);
    }

    @Override
    public void unregister(String name) {
        this.objects.remove(name);
    }

    @Override
    public Iterator<T> iterator() {
        return this.objects.values().iterator();
    }
}
