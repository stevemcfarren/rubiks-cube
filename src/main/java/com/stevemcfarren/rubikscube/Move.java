package com.stevemcfarren.rubikscube;

import java.util.Objects;

import com.stevemcfarren.rubikscube.RubiksCube.Face;

/**
 * Represents a single move to rotate a face of the Rubik's Cube.
 * A move consists of a face to rotate and an angle of rotation.
 * 
 * @author Steve McFarren
 */
public final class Move {

	private Face face;
	private int angle;
	private Type type;
	
	public enum Type {
		FACE, CUBE
	};


	public Move() {
		this.face = Face.TOP;
		this.angle = 90;
		this.type = Type.FACE;
	}

	/**
	 * Constructs a new face rotation Move with the specified face and rotation angle.
	 *
	 * @param face the face to rotate
	 * @param angle the rotation angle in degrees (positive is clockwise, negative is counter-clockwise)
	 */
	public Move(Face face, int angle) {
		this.face = face;
		this.angle = angle;
		this.type = Type.FACE;
	}

	/**
	 * Constructs a new Move with the specified type, face and rotation angle.
	 *
	 * @param face the face to rotate
	 * @param angle the rotation angle in degrees (positive is clockwise, negative is counter-clockwise)
	 * @param type identifies the type of move (face rotation or whole cube rotation).
	 */
	public Move(Type type, Face face, int angle) {
		this.type = type;
		this.face = face;
		this.angle = angle;
	}

	/**
	 * Gets the face that this move rotates.
	 *
	 * @return the face to be rotated
	 */
	public Face getFace() {
		return face;
	}
	
	/**
	 * Gets the rotation angle of this move.
	 *
	 * @return the rotation angle in degrees
	 */
	public int getAngle() {
		return angle;
	}

	/**
	 * Gets the type of move.
	 *
	 * @return the move type
	 */
	public Type getType() {
		return type;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(face, angle);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Move other = (Move) obj;
		return face == other.face && angle == other.angle && type == other.type;
	}

	@Override
	public String toString() {
		return type + ": " + face + ", " + angle;
	}
}
