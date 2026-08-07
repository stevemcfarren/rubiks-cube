package com.stevemcfarren.rubikscube;

/**
 * Represents the colors used on a Rubik's Cube. Each color has an associated
 * bitmask for identification and an RGB hex value for display. The bitmask
 * values are powers of 2 to allow unique identification of color combinations.
 */
public enum Color {
	/**
	 * No color (used for center and edge pieces that don't have a sticker on a
	 * particular axis)
	 */
	NONE(-1, 0, ""),
	/** White color, typically on the top face */
	WHITE(0, 1, "EEEEEE"),
	/** Red color, typically on the left face */
	RED(1, 2, "FF0000"),
	/** Blue color, typically on the front face */
	BLUE(2, 4, "0000FF"),
	/** Orange color, typically on the right face */
	ORANGE(3, 8, "FF8000"),
	/** Green color, typically on the back face */
	GREEN(4, 16, "00FF00"),
	/** Yellow color, typically on the bottom face */
	YELLOW(5, 32, "FFFF00");

	/** ID value used for unique identification of this color */
	public final int id;
	/** Bitmask value used for unique identification of color combinations */
	public final int bitmask;
	/** RGB hex string for display purposes */
	public final String rgb;

	/**
	 * Constructs a new Color with the specified ID, bitmask and RGB value.
	 *
	 * @param id      the unique identifier for this color
	 * @param bitmask the unique bitmask identifier for this color
	 * @param rgb     the RGB hex string for display
	 */
	Color(int id, int bitmask, String rgb) {
		this.id = id;
		this.bitmask = bitmask;
		this.rgb = rgb;
	}

	/**
	 * Find the Color with the given id.
	 *
	 * @param id the unique identifier of the desired color.
	 */
	public static Color getById(final int id) {
		for (Color c : values()) {
			if (c.id == id) {
				return c;
			}
		}
		throw new IllegalArgumentException(); // or return null if you want
	}

}
