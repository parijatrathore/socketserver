package com.socketserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPRequest {

	public enum RequestType {
		GET, POST, PUT, DELETE;
	}

	public static class Url {
		private String				pathName;
		private String				queryString;
		private String				route;
		private Map<String, String>	params	= new HashMap<>();

		public Url(String route) {
			this.route = route;
			String[] parts = route.split("\\?");
			this.pathName = parts[0];
			if (parts.length > 1) {
				this.queryString = parts[1];
				for (String param : this.queryString.split("&")) {
					String[] keyValue = param.split("=");
					params.put(keyValue[0], keyValue[1]);
				}
			}
		}

		public String getRoute() {
			return route;
		}

		public String getPathName() {
			return pathName;
		}

		public String getQueryString() {
			return queryString;
		}

		public Map<String, String> getParams() {
			return params;
		}

		public int length() {
			return route.length();
		}

		@Override
		public String toString() {
			return route;
		}
	}

	private RequestType		requestType;
	private Url				url;
	private List<Header>	headers	= new ArrayList<>();

	public HTTPRequest(InputStream stream) throws IOException {
		InputStreamReader raw = new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(raw);
		String inputLine = reader.readLine();
		while (inputLine != null && !inputLine.isEmpty()) {
			// System.out.println(inputLine);
			if (requestType == null) {
				parseRouteAndType(inputLine);
			} else {
				headers.add(Header.parse(inputLine));
			}
			inputLine = reader.readLine();
		}

	}

	private void parseRouteAndType(String value) {
		String[] values = value.split(" ");
		requestType = RequestType.valueOf(values[0]);
		url = new Url(values[1]);
	}

	public RequestType getRequestType() {
		return requestType;
	}

	public Url getUrl() {
		return url;
	}

	public List<Header> getHeaders() {
		return headers;
	}
}
