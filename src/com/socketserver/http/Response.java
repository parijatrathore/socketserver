package com.socketserver.http;

public class Response {

	public enum Type {
		OK("ok"), KILLED("killed"), EXPIRED("expired");

		private String	name;

		Type(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public Response(Type stat) {
		this.stat = stat;
	}

	private Type	stat;

	public String getStat() {
		return stat.getName();
	}

	public void setStat(String stat) {
		this.stat = Type.valueOf(stat);
	}

}
