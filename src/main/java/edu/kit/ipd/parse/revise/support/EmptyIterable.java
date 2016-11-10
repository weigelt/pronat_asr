package edu.kit.ipd.parse.revise.support;

import java.util.Iterator;

public class EmptyIterable<T> implements Iterable<T> {
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			public boolean hasNext() {
					return false;
			}
			
			public T next() {
				return null;
			}
		};
	}
}
