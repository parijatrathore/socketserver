package com.socketserver.utils;

public class Charset {

	public enum Type {
		UTF8("UTF-8");
		private String	name;

		Type(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

	/*
	 * private static final Map<String, Charset> BY_VALUE =
	 * Arrays.asList(Charset.values()) .stream()
	 * .collect(Collectors.toMap(Charset::getValue, ct -> ct));
	 */

	private String	value;

	private Charset(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	/*
	 * public static Charset getByValue(String value) { return
	 * BY_VALUE.get(value); }
	 */
}
