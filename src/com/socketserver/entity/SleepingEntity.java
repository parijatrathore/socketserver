package com.socketserver.entity;

import java.net.Socket;

public class SleepingEntity implements Comparable<SleepingEntity> {

	private String		connid;
	private long		creationTime;
	private Integer		timetoExpire;
	private long		expiryTime;
	private Thread		myThread;
	private Socket		mySocket;
	private Runnable	runnable;

	public SleepingEntity(String connid, long creationTime, Integer timetoExpire, Thread thread, Socket socket) {
		this.connid = connid;
		this.creationTime = creationTime;
		this.timetoExpire = timetoExpire;
		this.myThread = thread;
		this.mySocket = socket;
	}

	public SleepingEntity(String connid, long creationTime, Integer timetoExpire, Runnable runnable) {
		this.connid = connid;
		this.creationTime = creationTime;
		this.timetoExpire = timetoExpire;
		this.runnable = runnable;
	}

	public String getConnid() {
		return connid;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public Integer getTimetoExpire() {
		return timetoExpire;
	}

	public Thread getMyThread() {
		return myThread;
	}

	public Socket getMySocket() {
		return mySocket;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public long getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(long expiryTime) {
		this.expiryTime = expiryTime;
	}

	@Override
	public int compareTo(SleepingEntity other) {
		return 0;
	}

}
