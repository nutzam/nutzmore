package org.nutz.plugins.webqq.model;

import org.nutz.json.Json;

/**
 * 字体
 * 
 * @author ScienJus
 * @date 15/12/19.
 */
public class Font {

	public static final Font DEFAULT_FONT = defaultFont();

	private static Font defaultFont() {
		Font font = new Font();
		font.setColor("000000");
		font.setStyle(new int[] { 0, 0, 0 });
		font.setName("宋体");
		font.setSize(10);
		return font;
	}

	private int[] style;

	private String color;

	private String name;

	private int size;

	/**
	 * @return the style
	 */
	public int[] getStyle() {
		return style;
	}

	/**
	 * @param style
	 *            the style to set
	 */
	public void setStyle(int[] style) {
		this.style = style;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(String color) {
		this.color = color;
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
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Json.toJson(this);
	}
}
