package net.sentientturtle.util.tuple;

/**
 * 4 value tuple class
 * @param <T1> Type of {@link Tuple4#v1}
 * @param <T2> Type of {@link Tuple4#v2}
 * @param <T3> Type of {@link Tuple4#v3}
 * @param <T4> Type of {@link Tuple4#v4}
 */
public class Tuple4<T1, T2, T3, T4> {
    public final T1 v1;
    public final T2 v2;
    public final T3 v3;
    public final T4 v4;

    public Tuple4(T1 v1, T2 v2, T3 v3, T4 v4) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
    }

    public String toString() {
        return "(" + String.valueOf(v1) + ", " + String.valueOf(v2) + ", " + String.valueOf(v3) + ", " + String.valueOf(v4) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple4<?, ?, ?, ?> tuple4 = (Tuple4<?, ?, ?, ?>) o;

        if (v1 != null ? !v1.equals(tuple4.v1) : tuple4.v1 != null) return false;
        if (v2 != null ? !v2.equals(tuple4.v2) : tuple4.v2 != null) return false;
        if (v3 != null ? !v3.equals(tuple4.v3) : tuple4.v3 != null) return false;
        return v4 != null ? v4.equals(tuple4.v4) : tuple4.v4 == null;
    }

    @Override
    public int hashCode() {
        int result = v1 != null ? v1.hashCode() : 0;
        result = 31 * result + (v2 != null ? v2.hashCode() : 0);
        result = 31 * result + (v3 != null ? v3.hashCode() : 0);
        result = 31 * result + (v4 != null ? v4.hashCode() : 0);
        return result;
    }
}
