package net.sentientturtle.util.tuple;

/**
 * 3 value tuple class
 * @param <T1> Type of {@link Tuple3#v1}
 * @param <T2> Type of {@link Tuple3#v2}
 * @param <T3> Type of {@link Tuple3#v3}
 */
public class Tuple3<T1, T2, T3> {
    public final T1 v1;
    public final T2 v2;
    public final T3 v3;

    public Tuple3(T1 v1, T2 v2, T3 v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public String toString() {
        return "(" + String.valueOf(v1) + ", " + String.valueOf(v2) + ", " + String.valueOf(v3) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;
        if (v1 != null ? !v1.equals(tuple3.v1) : tuple3.v1 != null) return false;
        if (v2 != null ? !v2.equals(tuple3.v2) : tuple3.v2 != null) return false;
        return v3 != null ? v3.equals(tuple3.v3) : tuple3.v3 == null;
    }

    @Override
    public int hashCode() {
        int result = v1 != null ? v1.hashCode() : 0;
        result = 31 * result + (v2 != null ? v2.hashCode() : 0);
        result = 31 * result + (v3 != null ? v3.hashCode() : 0);
        return result;
    }
}
