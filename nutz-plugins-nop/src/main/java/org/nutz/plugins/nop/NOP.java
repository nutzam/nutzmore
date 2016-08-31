package org.nutz.plugins.nop;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file NOP.java
 *
 * @description
 *
 * @time 2016年8月31日 下午3:33:15
 *
 */
public class NOP {

	public static final String name = "nop";
	public static final String description = "nutz open platform";
	public static final int majorVersion = 1;
	public static final int minorVersion = 0;
	public static final boolean snapshot = true;
	public static final String releaseLevel = "b";

	public static String name() {
		return name;
	}

	public static String v() {
		return String.format("%d.%s.%d%s",
				majorVersion(),
				releaseLevel(),
				minorVersion(), snapshot() ? ".SNAPSHOT" : "");
	}

	public static String description() {
		return description;
	}

	public static boolean snapshot() {
		return snapshot;
	}

	public static int majorVersion() {
		return majorVersion;
	}

	public static int minorVersion() {
		return minorVersion;
	}

	public static String releaseLevel() {
		return releaseLevel;
	}

}
