package com.socketserver.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.socketserver.cache.InMemoryEvictionCache;
import com.socketserver.entity.SleepingEntity;

public class HTTPServer {
	static ServerSocket								server;

	static int										port						= 3030;

	static ThreadPoolExecutor						executor					= new ThreadPoolExecutor(20, Integer.MAX_VALUE, 120, TimeUnit.SECONDS,
																						new LinkedBlockingQueue<Runnable>());
	static ScheduledThreadPoolExecutor				scheduledThreadPoolExecutor	= new ScheduledThreadPoolExecutor(Integer.MAX_VALUE);

	static InMemoryEvictionCache<SleepingEntity>	cache						= new InMemoryEvictionCache<>();

	static Timer									timer						= new Timer();

	public static void main(String[] args) {

		try {
			server = new ServerSocket(port);
			System.out.println("Server started on port [" + port + "]. Listening for connections.");
			timer.scheduleAtFixedRate(new TimerTask() {
				int	activeConns	= 0;

				@Override
				public void run() {
					for (String key : cache.getKeys()) {
						if (cache.get(key) != null) {
							activeConns++;
						}
					}
					System.out.println("Current active connections " + activeConns);
				}
			}, 1000, 1000);
			while (true) {
				// Socket clientSocket = server.accept();
				executor.submit(new RequestProcessor(server.accept(), cache, scheduledThreadPoolExecutor));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
