package org.nutz.plugins.dict;

public class Selects {
	private Selects() {
		super();
	}

	public static SelectBuilder custom() {
		return SelectBuilder.create();
	}

	public static void createDefault() {
		SelectBuilder.create().build();
	}

	public static void createSystem() {
		SelectBuilder.create().useSystemProperties().build();
	}
}
