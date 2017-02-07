package com.socketserver.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.google.gson.Gson;
import com.socketserver.cache.InMemoryEvictionCache;
import com.socketserver.entity.SleepingEntity;
import com.socketserver.http.HTTPRequest;
import com.socketserver.http.Response;
import com.socketserver.http.Response.Type;
import com.socketserver.utils.AppConstants;
import com.nicobar.core.utils.DateUtils;

public class RequestProcessor implements Runnable {

	private Socket									clientSocket;
	private InMemoryEvictionCache<SleepingEntity>	cache;
	private ScheduledThreadPoolExecutor				poolExecutor;

	public RequestProcessor(Socket socket, InMemoryEvictionCache<SleepingEntity> cache, ScheduledThreadPoolExecutor poolExecutor) {
		this.clientSocket = socket;
		this.cache = cache;
		this.poolExecutor = poolExecutor;
	}

	@Override
	public void run() {
		try {
			System.out.println("Request served from " + Thread.currentThread().getName());
			String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
			Response response;
			HTTPRequest request = new HTTPRequest(clientSocket.getInputStream());
			if ("/sleep".equals(request.getUrl().getPathName())) {
				// NEW
				/*
				 * Runnable runnable = new SleepEntityRunnable(clientSocket,
				 * request
				 * .getUrl().getParams().get(AppConstants.CONNECTION_ID));
				 * 
				 * SleepingEntity entity = new
				 * SleepingEntity(request.getUrl().getParams
				 * ().get(AppConstants.CONNECTION_ID),
				 * DateUtils.getCurrentTime().getTime(),
				 * Integer.valueOf(request.
				 * getUrl().getParams().get(AppConstants.TIMEOUT)), runnable);
				 * cache.set(entity.getConnid(), entity.getTimetoExpire(),
				 * entity); poolExecutor.scheduleWithFixedDelay(runnable, 0,
				 * entity.getTimetoExpire(), TimeUnit.SECONDS);
				 */
				// NEW
				try {
					handleSleepRequest(request);
					response = new Response(Type.OK);
					httpResponse = httpResponse + new Gson().toJson(response);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if ("/server-status".equals(request.getUrl().getPathName())) {
				Map<String, String> responseMap = new HashMap<>();
				SleepingEntity cacheEntity = null;
				for (String key : cache.getKeys()) {
					cacheEntity = cache.get(key);
					if (cacheEntity != null) {
						responseMap.put(key, String.valueOf((cacheEntity.getExpiryTime() - DateUtils.getCurrentTime().getTime()) / 1000));
					}
				}
				httpResponse = httpResponse + new Gson().toJson(responseMap);
				System.out.println(httpResponse);
			} else if ("/kill".equals(request.getUrl().getPathName())) {
				String connId = request.getUrl().getParams().get(AppConstants.CONNECTION_ID);
				SleepingEntity entity = cache.get(connId);
				/*
				 * SleepEntityRunnable runnable = (SleepEntityRunnable)
				 * entity.getRunnable(); runnable.setInteruppted(true);
				 */
				if (entity != null) {
					response = new Response(Type.KILLED);
					httpResponse = httpResponse + new Gson().toJson(response);
					entity.getMySocket().getOutputStream().write(httpResponse.getBytes("UTF-8"));
					entity.getMySocket().close();
					cache.remove(connId);
					entity.getMyThread().interrupt();
					response = new Response(Type.OK);
				} else {
					response = new Response(Type.EXPIRED);
				}
				httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + new Gson().toJson(response);
			} else {

			}
			/*
			 * InputStreamReader isr = new
			 * InputStreamReader(clientSocket.getInputStream()); BufferedReader
			 * reader = new BufferedReader(isr); String line =
			 * reader.readLine(); while (line != null && !line.isEmpty()) {
			 * System.out.println(line); line = reader.readLine(); }
			 */
			Date date = DateUtils.getCurrentTime();
			clientSocket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleSleepRequest(HTTPRequest request) throws InterruptedException {
		SleepingEntity entity = new SleepingEntity(request.getUrl().getParams().get(AppConstants.CONNECTION_ID), DateUtils.getCurrentTime().getTime(),
				Integer.valueOf(request.getUrl().getParams().get(AppConstants.TIMEOUT)), Thread.currentThread(), clientSocket);
		entity.setExpiryTime(entity.getCreationTime() + entity.getTimetoExpire() * 1000);
		synchronized (entity.getConnid()) {
			cache.set(entity.getConnid(), entity.getTimetoExpire(), entity);
		}
			
		}
		Thread.sleep(entity.getTimetoExpire() * 1000);
	}

}
