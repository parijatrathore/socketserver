package com.socketserver.entity;

import java.net.Socket;

public class SleepEntityRunnable implements Runnable {

	private Socket	mysocket;
	private boolean	isInteruppted;
	private String	connectionId;

	public SleepEntityRunnable(Socket mysocket, String connectionId) {
		this.mysocket = mysocket;
		this.connectionId = connectionId;
	}


	@Override
	public void run() {
		// TODO write resposne on socket based on interupption

	}

	public boolean isInteruppted() {
		return isInteruppted;
	}

	public void setInteruppted(boolean isInteruppted) {
		this.isInteruppted = isInteruppted;
	}

}
