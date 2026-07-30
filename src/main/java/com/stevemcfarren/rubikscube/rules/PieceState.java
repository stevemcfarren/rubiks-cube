package com.stevemcfarren.rubikscube.rules;

import com.stevemcfarren.rubikscube.Color;
import com.stevemcfarren.rubikscube.Piece;
import com.stevemcfarren.rubikscube.Point3D;

/**
 * Represents the state of a piece at a specific location on the cube. This
 * class captures both the piece's colors (identifying the piece and its
 * orientation) and its current 3D position on the cube.
 */
public class PieceState {
	/** Unique identifier for the piece based on its colors */
	public final int pieceID;
	/** The color of the piece on the X-axis, or null if not applicable */
	public final Color xColor;
	/** The color of the piece on the Y-axis, or null if not applicable */
	public final Color yColor;
	/** The color of the piece on the Z-axis, or null if not applicable */
	public final Color zColor;
	/** The current 3D location of the piece on the cube */
	public final Point3D location;

	/**
	 * Constructs a PieceState from a piece and its location.
	 *
	 * @param piece    the piece to capture state for
	 * @param location the location of the piece on the cube
	 */
	public PieceState(Piece piece, Point3D location) {
		this.pieceID = piece.getID();
		this.xColor = piece.getXColor();
		this.yColor = piece.getYColor();
		this.zColor = piece.getZColor();
		this.location = location;
	}

	/**
	 * Constructs a PieceState with explicit colors and location.
	 *
	 * @param xColor   the color on the X-axis
	 * @param yColor   the color on the Y-axis
	 * @param zColor   the color on the Z-axis
	 * @param location the location of the piece on the cube
	 */
	public PieceState(Color xColor, Color yColor, Color zColor, Point3D location) {
		this.pieceID = xColor.bitmask + yColor.bitmask + zColor.bitmask;
		this.xColor = xColor;
		this.yColor = yColor;
		this.zColor = zColor;
		this.location = location;
	}

	/**
	 * Constructs a PieceState with only a piece ID and location. This is used when
	 * only piece identification is needed and the piece's orientation is not
	 * important.
	 *
	 * @param pieceID  the unique identifier for the piece
	 * @param location the location of the piece on the cube
	 */
	public PieceState(int pieceID, Point3D location) {
		this.pieceID = pieceID;
		this.xColor = null;
		this.yColor = null;
		this.zColor = null;
		this.location = location;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PieceState other = (PieceState) obj;
		return pieceID == other.pieceID && xColor == other.xColor && yColor == other.yColor && zColor == other.zColor
				&& location.equals(other.location);
	}

	/**
	 * Checks if this piece state matches the given piece. Comparison is done by
	 * colors if available, otherwise by piece ID.
	 *
	 * @param p the piece to compare against
	 * @return true if the piece matches this state, false otherwise
	 */
	public boolean matches(Piece p) {
		if (xColor != null && yColor != null && zColor != null) {
			return xColor == p.getXColor() && yColor == p.getYColor() && zColor == p.getZColor();
		}

		return pieceID == p.getID();
	}

	/**
	 * Reconstructs a Piece from this piece state.
	 *
	 * @return a new Piece with the colors from this state
	 */
	public Piece getPiece() {
		return new Piece(xColor, yColor, zColor);
	}

	@Override
	public String toString() {
		return "(" + xColor + ", " + yColor + ", " + zColor + ") @ " + location;
	}

}
