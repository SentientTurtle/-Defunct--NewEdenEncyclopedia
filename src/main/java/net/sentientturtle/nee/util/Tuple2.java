package net.sentientturtle.nee.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * 2 value tuple class
 * @param <T1> Type of {@link Tuple2#v1}
 * @param <T2> Type of {@link Tuple2#v2}
 */
public class Tuple2<T1, T2> {
    public final T1 v1;
    public final T2 v2;

    public Tuple2(T1 v1, T2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public String toString() {
        return "(" + String.valueOf(v1) + ", " + String.valueOf(v2) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;

        if (v1 != null ? !v1.equals(tuple2.v1) : tuple2.v1 != null) return false;
        return v2 != null ? v2.equals(tuple2.v2) : tuple2.v2 == null;
    }

    @Override
    public int hashCode() {
        int result = v1 != null ? v1.hashCode() : 0;
        result = 31 * result + (v2 != null ? v2.hashCode() : 0);
        return result;
    }

    // Utility methods for use in streams, allows for easy stream collection to maps, at the cost of object thrashing.

    /**
     * Maps {@link Map.Entry} objects to {@link Tuple2} objects
     * @param entry Entry to map to a tuple
     * @param <T1> Type of {@link Tuple2#v1} / {@link Map.Entry#getKey()}
     * @param <T2> Type of {@link Tuple2#v2} / {@link Map.Entry#getValue()}
     * @return Tuple equivalent of the specified entry, where {@link Tuple2#v1} is the key of the entry, and {@link Tuple2#v2} is the value of the entry.
     */
    public static <T1, T2> Tuple2<T1, T2> mapFromEntry(Map.Entry<T1, T2> entry) {
        return new Tuple2<>(entry.getKey(), entry.getValue());
    }

    /**
     * Builds a collector that collects a Tuple2 stream to a {@link HashMap}
     * @param <T1> Type of {@link Tuple2#v1}
     * @param <T2> Type of {@link Tuple2#v2}
     * @return A collector that collects a Tuple2 stream to a {@link HashMap}
     */
    public static <T1, T2> Collector<Tuple2<T1, T2>, HashMap<T1, T2>, HashMap<T1, T2>> collectToMap() {
        return Collector.of(HashMap::new,
                (map, tuple) -> map.put(tuple.v1, tuple.v2),
                (map, map2) -> {
                    map.putAll(map2);
                    return map;
                },
                Collector.Characteristics.UNORDERED);
    }

    /**
     * Builds a collector that collects a Tuple2 stream to a supplied map
     * @param mapSupplier MapSupplier that provides a map to collect to.
     * @param <T1> Type of {@link Tuple2#v1}
     * @param <T2> Type of {@link Tuple2#v2}
     * @return A collector that collects a Tuple2 stream to a {@link Map}
     */
    public static <T1, T2> Collector<Tuple2<T1, T2>, Map<T1, T2>, Map<T1, T2>> collectToMap(Supplier<Map<T1, T2>> mapSupplier) {
        return Collector.of(mapSupplier,
                (map, tuple) -> map.put(tuple.v1, tuple.v2),
                (map, map2) -> {
                    map.putAll(map2);
                    return map;
                },
                Collector.Characteristics.UNORDERED);
    }

    /**
     * A Tuple2 {@link Consumer} that unpacks Tuple2, providing functionality similar to {@link BiConsumer}
     * @param <T1> Type of {@link Tuple2#v1}
     * @param <T2> Type of {@link Tuple2#v2}
     */
    public interface BiConsumer<T1, T2> extends java.util.function.BiConsumer<T1, T2>, Consumer<Tuple2<T1, T2>> {
        @Override
        default void accept(Tuple2<T1, T2> tuple) {
            accept(tuple.v1, tuple.v2);
        }
    }
}
