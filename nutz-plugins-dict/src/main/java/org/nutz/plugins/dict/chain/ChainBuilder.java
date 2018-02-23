package org.nutz.plugins.dict.chain;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

final class ChainBuilder<E> {

    private final LinkedList<E> list;
    private final Map<Class<?>, E> uniqueClasses;

    public ChainBuilder() {
        this.list = new LinkedList<E>();
        this.uniqueClasses = new HashMap<Class<?>, E>();
    }

    private void ensureUnique(final E e) {
        final E previous = this.uniqueClasses.remove(e.getClass());
        if (previous != null) {
            this.list.remove(previous);
        }
        this.uniqueClasses.put(e.getClass(), e);
    }

    public ChainBuilder<E> addFirst(final E e) {
        if (e == null) {
            return this;
        }
        ensureUnique(e);
        this.list.addFirst(e);
        return this;
    }

    public ChainBuilder<E> addLast(final E e) {
        if (e == null) {
            return this;
        }
        ensureUnique(e);
        this.list.addLast(e);
        return this;
    }

    public ChainBuilder<E> addAllFirst(final Collection<E> c) {
        if (c == null) {
            return this;
        }
        for (final E e: c) {
            addFirst(e);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
	public ChainBuilder<E> addAllFirst(final E... c) {
        if (c == null) {
            return this;
        }
        for (final E e: c) {
            addFirst(e);
        }
        return this;
    }

    public ChainBuilder<E> addAllLast(final Collection<E> c) {
        if (c == null) {
            return this;
        }
        for (final E e: c) {
            addLast(e);
        }
        return this;
    }
    
    @SuppressWarnings("unchecked")
    public ChainBuilder<E> addAllLast(final E... c) {
        if (c == null) {
            return this;
        }
        for (final E e: c) {
            addLast(e);
        }
        return this;
    }

    public LinkedList<E> build() {
        return new LinkedList<E>(this.list);
    }

}
