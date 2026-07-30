package com.stevemcfarren.rubikscube;

/**
 * Represents the colors used on a Rubik's Cube.
 * Each color has an associated bitmask for identification and an RGB hex value for display.
 * The bitmask values are powers of 2 to allow unique identification of color combinations.
 */
public enum Color {
	/** No color (used for center and edge pieces that don't have a sticker on a particular axis) */
	NONE(0, ""),
	/** White color, typically on the top face */
	WHITE(1, "EEEEEE"),
	/** Red color, typically on the left face */
	RED(2, "FF0000"),
	/** Blue color, typically on the front face */
	BLUE(4, "0000FF"),
	/** Orange color, typically on the right face */
	ORANGE(8, "FF8000"),
	/** Green color, typically on the back face */
	GREEN(16, "00FF00"),
	/** Yellow color, typically on the bottom face */
	YELLOW(32, "FFFF00");

	/** Bitmask value used for unique identification of this color */
	public final int bitmask;
	/** RGB hex string for display purposes */
	public final String rgb;

	/**
	 * Constructs a new Color with the specified bitmask and RGB value.
	 *
	 * @param bitmask the unique bitmask identifier for this color
	 * @param rgb the RGB hex string for display
	 */
	Color(int bitmask, String rgb) {
		this.bitmask = bitmask;
		this.rgb = rgb;
	}
}
