package com.stevemcfarren.rubikscube;


import java.util.Objects;

import com.stevemcfarren.rubikscube.Point3D.Axis;


/**
 * Represents a single piece of a Rubik's Cube.
 * A piece can be a corner (3 colors), edge (2 colors), or center (1 color).
 * The colors are stored based on the piece's position relative to the X, Y, and Z axes.
 * 
 * @author Steve McFarren
 */
public class Piece {

	private Color xColor;
	private Color yColor;
	private Color zColor;

	@Override
	public int hashCode() {
		return Objects.hash(xColor, yColor, zColor);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Piece other = (Piece) obj;
		return xColor == other.xColor && yColor == other.yColor && zColor == other.zColor;
	}

	@Override
	public String toString() {
		return "(" + xColor + ", " + yColor + ", " + zColor + ")";
	}

	/**
	 * Gets a unique identifier for this piece based on its colors.
	 * The ID is calculated by adding the bitmask values of all three colors.
	 *
	 * @return the unique piece identifier
	 */
	public int getID() {
		return xColor.bitmask + yColor.bitmask + zColor.bitmask;
	}

	/**
	 * Gets the color of this piece on the X-axis.
	 *
	 * @return the color on the X face, or Color.NONE if not applicable
	 */
	public Color getXColor() {
		return this.xColor;
	}

	/**
	 * Gets the color of this piece on the Y-axis.
	 *
	 * @return the color on the Y face, or Color.NONE if not applicable
	 */
	public Color getYColor() {
		return this.yColor;
	}

	/**
	 * Gets the color of this piece on the Z-axis.
	 *
	 * @return the color on the Z face, or Color.NONE if not applicable
	 */
	public Color getZColor() {
		return this.zColor;
	}
	
	/**
	 * Checks if this piece has the specified color.
	 *
	 * @param c the color to check for
	 * @return true if the piece has the specified color, false otherwise
	 */
	public boolean hasColor(Color c) {
		return (c.bitmask & getID()) > 0;
	}

	/**
	 * Creates a copy of the given piece.
	 *
	 * @param p the piece to copy
	 */
	public Piece(Piece p) {
		this.xColor = p.xColor;
		this.yColor = p.yColor;
		this.zColor = p.zColor;
	}

	/**
	 * Constructs a new Piece with the specified colors.
	 * At least one color must not be Color.NONE, and no two colors can be the same.
	 *
	 * @param xColor the color on the X face
	 * @param yColor the color on the Y face
	 * @param zColor the color on the Z face
	 * @throws IllegalArgumentException if the colors are invalid (all NONE or duplicate colors)
	 */
	public Piece(Color xColor, Color yColor, Color zColor) {
		validateColor(xColor, yColor, zColor);
		this.xColor = xColor;
		this.yColor = yColor;
		this.zColor = zColor;
	}

	/**
	 * Rotate this piece along the given axis. If face is specified only pieces
	 * within that face are rotated.
	 * 
	 * @param axis  the axis around which to rotate the piece.
	 * @param angle the angle and direction of rotation. Must be a multiple of 90
	 *              between -90 and 270 (negative is counter-clockwise).
	 * @throws IllegalArgumentExeption if angle is not a multiple of 90
	 *              between -90 and 270.
	 */
	public void rotate(Axis axis, int angle) {
		// Validate angle (allow -180 to simplify 'undoing' a move).
		if (angle < -90 || angle > 270 || (angle % 90) != 0) {
			throw new IllegalArgumentException("'Angle' multiple of 90 between -270 and 270: " + angle);
		}

		// Nothing to do if rotating a multiple of 180.
		if (angle % 180 == 0) {
			return;
		}
		
		switch (axis) {
		case X:
			Color temp = yColor;
			yColor = zColor;
			zColor = temp;
			break;
		case Y:
			temp = xColor;
			xColor = zColor;
			zColor = temp;
			break;
		case Z:
		default:
			temp = xColor;
			xColor = yColor;
			yColor = temp;
		}
	}
	
	private void validateColor(Color xColor, Color yColor, Color zColor) {
		if (xColor == Color.NONE && yColor == Color.NONE && zColor == Color.NONE) {
			throw new IllegalArgumentException(
					"At least one color must not be 'None'.");			
		}
		if ((xColor != Color.NONE && (xColor == yColor || xColor == zColor)) || (yColor != Color.NONE && yColor == zColor)) {
			throw new IllegalArgumentException(
					"Two sides cannot be the same color: " + xColor + ", " + yColor + ", " + zColor);			
		}
	}
}
