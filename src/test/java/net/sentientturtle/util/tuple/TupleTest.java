package net.sentientturtle.util.tuple;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TupleTest {
    @Test
    void testTuple2FromEntry() {
        Object key = new Object();
        Object value = new Object();
        Tuple2<Object, Object> tuple2 = Tuple2.mapFromEntry(new Map.Entry<Object, Object>() {
            @Override
            public Object getKey() {
                return key;
            }

            @Override
            public Object getValue() {
                return value;
            }

            @Override
            public Object setValue(Object value) {
                return null;
            }
        });

        assertEquals(tuple2, new Tuple2<>(key, value));
    }

    @Test
    void testTuple2CollectToMap() {
        //noinspection unchecked
        HashSet<Tuple2<Integer, Integer>> set = new HashSet<>(100);
        for (int i = 0; i < 100; i++) {
            //noinspection unchecked
            set.add(new Tuple2(i, i*2));
        }
        HashMap<Integer, Integer> map = set.stream().collect(Tuple2.collectToMap());
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            assertEquals(entry.getKey() * 2, entry.getValue().intValue());
            assertTrue(set.contains(new Tuple2<>(entry.getKey(), entry.getValue())));
        }
    }
}
