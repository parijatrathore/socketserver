
package com.socketserver.cache;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.socketserver.entity.SleepingEntity;

public class InMemoryEvictionCache<T> {
	private final ConcurrentMap<String, CacheEntry<T>> _cache = new ConcurrentHashMap<String, CacheEntry<T>>();

	public void set(String key, int secondsToLive, T value) {
		_cache.put(key, new CacheEntry<T>(key, value, secondsToLive));
	}

	public T get(String key) {
		CacheEntry<T> cacheEntry = _cache.get(key);
		if (cacheEntry != null && cacheEntry.getExpiryTime() > System.currentTimeMillis()) {
			return cacheEntry.getValue();
		} else {
			remove(key);
			return null;
		}
	}

	public Set<String> getKeys() {
		return _cache.keySet();
	}

	public void remove(String key) {
		CacheEntry<SleepingEntity> entity = (CacheEntry<SleepingEntity>) _cache.get(key);
		synchronized (entity.getValue().getConnid()) {
			_cache.remove(key);
		}
	}

	private class CacheEntry<V> {

		private final V    value;
		private final long createTime;
		private final long expiryTime;

		/**
		 *
		 */
		public CacheEntry(String key, V value, int secondsToLive) {
			this.value = value;
			this.createTime = System.currentTimeMillis();
			this.expiryTime = this.createTime + secondsToLive * 1000;
		}

		/**
		 * @return the value
		 */
		public V getValue() {
			return value;
		}

		/**
		 * @return the expiryTime
		 */
		public long getExpiryTime() {
			return expiryTime;
		}
	}
}
