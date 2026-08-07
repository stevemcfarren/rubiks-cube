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
	private Direction direction;
	private Type type;
	
	public enum Type {
		FACE, CUBE
	};

	public enum Direction {
		CLOCKWISE, COUNTERCLOCKWISE
	};


	public Move() {
		this.face = Face.TOP;
		this.direction = Direction.CLOCKWISE;
		this.type = Type.FACE;
	}

	/**
	 * Constructs a new Move with the specified type, face and rotation angle.
	 *
	 * @param face the face to rotate
	 * @param angle the rotation angle in degrees (positive is clockwise, negative is counter-clockwise)
	 * @param type identifies the type of move (face rotation or whole cube rotation).
	 */
	public Move(Face face, Direction direction) {
		this.type = Type.FACE;
		this.face = face;
		this.direction = direction;
	}

	/**
	 * Constructs a new Move with the specified type, face and rotation angle.
	 *
	 * @param face the face to rotate
	 * @param angle the rotation angle in degrees (positive is clockwise, negative is counter-clockwise)
	 * @param type identifies the type of move (face rotation or whole cube rotation).
	 */
	public Move(Type type, Face face, Direction direction) {
		this.type = type;
		this.face = face;
		this.direction = direction;
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
	 * Gets the direction of this move.
	 *
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
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
		return Objects.hash(type, face, direction);
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
		return face == other.face && direction == other.direction && type == other.type;
	}

	@Override
	public String toString() {
		return type + ": " + face + ", " + direction;
	}
}
