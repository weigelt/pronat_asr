package edu.kit.ipd.pronat.revise.support;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class EmptyMap<K, V> implements Map<K, V> {
	private static final EmptyMap<?, ?> instance = new EmptyMap<>();
	private static final EmptySet<?> keySet = EmptySet.getInstance();
	private static final Collection<?> values = EmptySet.getInstance();
	private static final Set<?> entrySet = EmptySet.getInstance();

	public static <K, V> EmptyMap<K, V> getInstance() {
		return (EmptyMap<K, V>) instance;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean containsKey(Object key) {
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	@Override
	public V get(Object key) {
		return null;
	}

	@Override
	public V put(K key, V value) {
		return null;
	}

	@Override
	public V remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
	}

	@Override
	public void clear() {
	}

	@Override
	public Set<K> keySet() {
		return (Set<K>) keySet;
	}

	@Override
	public Collection<V> values() {
		return (Collection<V>) values;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return (Set<java.util.Map.Entry<K, V>>) entrySet;
	}
}
