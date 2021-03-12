package edu.kit.ipd.pronat.revise.support;

import java.util.Iterator;

public class EmptyIterable<T> implements Iterable<T> {
	@Override public Iterator<T> iterator() {
		return new Iterator<T>() {
			@Override public boolean hasNext() {
				return false;
			}

			@Override public T next() {
				return null;
			}
		};
	}
}
