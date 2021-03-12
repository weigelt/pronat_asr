package edu.kit.ipd.pronat.revise.support;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

public class EmptySet<T> extends EmptyIterable<T> implements Set<T> {
	private static final EmptySet<?> instance = new EmptySet<>();

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public Object[] toArray() {
		return new Object[0];
	}

	@Override
	public <E> E[] toArray(E[] a) {
		return (E[]) Array.newInstance(a.getClass(), 0);
	}

	@Override
	public boolean add(T e) {
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {
	}

	public static <T> EmptySet<T> getInstance() {
		return (EmptySet<T>) instance;
	}
}
