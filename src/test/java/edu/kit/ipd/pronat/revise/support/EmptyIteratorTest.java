package edu.kit.ipd.pronat.revise.support;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

public class EmptyIteratorTest {
	@Test
	public void test() {
		EmptyIterable<String> iterable = new EmptyIterable<>();

		Iterator<String> iter = iterable.iterator();

		assertNull(iter.next());

		assertFalse(iter.hasNext());
	}

}
