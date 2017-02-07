package com.socketserver.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockCache {

	private LockCache() {

	}

	private ConcurrentMap<String, LockEntity>	lockMap		= new ConcurrentHashMap<>();
	private static LockCache					_instance	= new LockCache();

	public static LockCache getInstance() {

		return _instance;
	}

	class LockEntity {

		private boolean	taken;
		private Lock	lock;

		public LockEntity(Lock lock) {
			super();
			this.lock = lock;
		}

		public boolean isTaken() {
			return taken;
		}

		public void setTaken(boolean taken) {
			this.taken = taken;
		}

		public Lock getLock() {
			return lock;
		}

	}

	public Lock getLock(String namespace) {
		Lock lock = null;
		synchronized (namespace) {
			if (lockMap.containsKey(namespace)) {
				synchronized (namespace) {
					LockEntity entity = new LockEntity(new ReentrantLock());
					lock = entity.getLock();
				}
			} else {
				lock = lockMap.get(namespace).getLock();
				if (!lock.tryLock()) {
					lock = null;
				}
			}

		}
		return lock;
	}

}
