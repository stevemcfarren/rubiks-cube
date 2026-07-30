package com.stevemcfarren.rubikscube;

import java.util.Objects;


/**
 * Represents a point in 3D space with integer coordinates.
 * Used to specify locations on a Rubik's Cube where each coordinate ranges from -1 to 1.
 * 
 * @author Steve McFarren
 */
public final class Point3D {
	/** The x-coordinate of the point */
	public final int x;
	/** The y-coordinate of the point */
	public final int y;
	/** The z-coordinate of the point */
	public final int z;

	public enum Axis {
		X, Y, Z
	}

	/**
	 * Constructs a new Point3D with the specified coordinates.
	 *
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 */
	public Point3D(int x, int y, int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point3D other = (Point3D) obj;
		return x == other.x && y == other.y && z == other.z;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	/**
	 * Calculate a new point following a rotation in 2D space around the given axis.
	 * 
	 * @param angle The angle to rotate the point in degrees (negative for clockwise).
	 * @param axis The axis (X, Y, Z) around which the point is rotated.
	 * 
	 * @return A new point with the rotated coordinates.
	 */
	public Point3D rotate(int angle, Axis axis) {
		double r = Math.toRadians(angle);
		double s = Math.sin(r);
		double c = Math.cos(r);

		int newX = 0;
		int newY = 0;
		int newZ = 0;
		
		switch (axis) {
		case X:
			newX = this.x;
			newY = (int)Math.round(this.z * s + this.y * c);
			newZ = (int)Math.round(this.z * c - this.y * s);
			break;
		case Y:
			newX = (int)Math.round(this.z * s + this.x * c);
			newY = this.y;
			newZ = (int)Math.round(this.z * c - this.x * s);
			break;
		case Z:
		default:
			newX = (int)Math.round(this.x * c - this.y * s);
			newY = (int)Math.round(this.x * s + this.y * c);
			newZ = this.z;
		}
		
		return new Point3D(newX, newY, newZ);
	}	

}
