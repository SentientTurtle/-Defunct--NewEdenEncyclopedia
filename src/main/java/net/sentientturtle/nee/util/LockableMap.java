package net.sentientturtle.nee.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Map view that can be locked; Swapping the backing map with an {@link Collections.UnmodifiableMap}
 * Utility class to allow the use of UnmodifiableMap in final variables.
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class LockableMap<K, V> implements Map<K, V>, Serializable {
    private static final long serialVersionUID = 1L;
    private Map<K, V> backingMap;
    private boolean locked;

    public LockableMap(Map<K, V> backingMap) {
        this.backingMap = backingMap;
    }

    public void lock() {
        if (!locked) {
            this.backingMap = Collections.unmodifiableMap(backingMap);
            locked = true;
        }
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public int size() {
        return backingMap.size();
    }

    @Override
    public boolean isEmpty() {
        return backingMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return backingMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return backingMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return backingMap.get(key);
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        return backingMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return backingMap.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        this.backingMap.putAll(m);
    }

    @Override
    public void clear() {
        backingMap.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return backingMap.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return backingMap.values();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return backingMap.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return backingMap.equals(o);
    }

    @Override
    public int hashCode() {
        return backingMap.hashCode();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return backingMap.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        backingMap.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        backingMap.replaceAll(function);
    }

    @Nullable
    @Override
    public V putIfAbsent(K key, V value) {
        return backingMap.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return backingMap.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return backingMap.replace(key, oldValue, newValue);
    }

    @Nullable
    @Override
    public V replace(K key, V value) {
        return backingMap.replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return backingMap.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return backingMap.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return backingMap.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return backingMap.merge(key, value, remappingFunction);
    }
}
