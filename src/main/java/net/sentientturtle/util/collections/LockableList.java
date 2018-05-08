package net.sentientturtle.util.collections;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.UnaryOperator;
/**
 * List view that can be locked; Swapping the backing list with an {@link Collections.UnmodifiableList}
 * Utility class to allow the use of UnmodifiableList in final variables.
 * @param <E> the type of values in this list
 */
public class LockableList<E> implements List<E> {
    private static final long serialVersionUID = 1L;
    private List<E> backingList;
    private boolean locked;

    public LockableList(List<E> backingList) {
        this.backingList = backingList;
    }

    public void lock() {
        if (!locked) {
            if (backingList instanceof ArrayList) ((ArrayList<E>) backingList).trimToSize();
            this.backingList = Collections.unmodifiableList(backingList);
            locked = true;
        }
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public boolean isEmpty() {
        return backingList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return backingList.contains(o);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return backingList.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return backingList.toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")  // It's just a wrapper
    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return backingList.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return backingList.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return backingList.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return backingList.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        return backingList.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends E> c) {
        return backingList.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return backingList.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return backingList.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        backingList.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        backingList.sort(c);
    }

    @Override
    public void clear() {
        backingList.clear();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")   // It's a wrapper
    @Override
    public boolean equals(Object o) {
        return backingList.equals(o);
    }

    @Override
    public int hashCode() {
        return backingList.hashCode();
    }

    @Override
    public E get(int index) {
        return backingList.get(index);
    }

    @Override
    public E set(int index, E element) {
        return backingList.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        backingList.add(index, element);
    }

    @Override
    public E remove(int index) {
        return backingList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return backingList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return backingList.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator() {
        return backingList.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator(int index) {
        return backingList.listIterator(index);
    }

    @NotNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return backingList.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<E> spliterator() {
        return backingList.spliterator();
    }
}
