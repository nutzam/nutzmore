package demo.bean;

import org.nutz.json.Json;
import org.nutz.lang.Lang;

/**
 * @author kerbores
 *
 */
public enum TT {

	T("T", 1),

	k("K", 2)

	;

	public static void main(String[] args) {
		System.err.println(Json.toJson(Lang.obj2map(T)));
	}

	String name;

	int value;

	/**
	 * @param name
	 * @param value
	 */
	private TT(String name, int value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}

}
