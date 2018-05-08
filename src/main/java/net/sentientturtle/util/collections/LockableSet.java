package net.sentientturtle.util.collections;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Set view that can be locked; Swapping the backing set with an {@link Collections.UnmodifiableSet}
 * Utility class to allow the use of UnmodifiableSet in final variables.
 * @param <E> the type of values in this set
 */
public class LockableSet<E> implements Set<E> {
    private static final long serialVersionUID = 1L;
    private Set<E> backingSet;
    private boolean locked;

    public LockableSet(Set<E> backingSet) {
        this.backingSet = backingSet;
    }

    public void lock() {
        if (!locked) {
            this.backingSet = Collections.unmodifiableSet(backingSet);
            locked = true;
        }
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public int size() {
        return backingSet.size();
    }

    @Override
    public boolean isEmpty() {
        return backingSet.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return backingSet.contains(o);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return backingSet.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return backingSet.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        //noinspection SuspiciousToArrayCall    We're just wrapping this
        return backingSet.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return backingSet.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return backingSet.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return backingSet.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        return backingSet.addAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return backingSet.retainAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return backingSet.removeAll(c);
    }

    @Override
    public void clear() {
        backingSet.clear();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")   // This is just a wrapper.
    @Override
    public boolean equals(Object o) {
        return backingSet.equals(o);
    }

    @Override
    public int hashCode() {
        return backingSet.hashCode();
    }

    @Override
    public Spliterator<E> spliterator() {
        return backingSet.spliterator();
    }
}
