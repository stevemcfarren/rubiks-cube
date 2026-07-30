package com.stevemcfarren.rubikscube.rules;

import java.util.List;

import com.stevemcfarren.rubikscube.Move;
import com.stevemcfarren.rubikscube.Piece;
import com.stevemcfarren.rubikscube.Point3D;
import com.stevemcfarren.rubikscube.RubiksCube;
import com.stevemcfarren.rubikscube.RubiksCube.Face;

public class RuleHelper {

	private RuleHelper() {
	}
	
	/** All possible face moves */
	public static final Move[] ALLMOVES = { new Move(Face.FRONT, 90), new Move(Face.FRONT, -90),
			new Move(Face.BACK, 90), new Move(Face.BACK, -90), new Move(Face.LEFT, 90),
			new Move(Face.LEFT, -90), new Move(Face.RIGHT, 90), new Move(Face.RIGHT, -90),
			new Move(Face.TOP, 90), new Move(Face.TOP, -90), new Move(Face.BOTTOM, 90),
			new Move(Face.BOTTOM, -90) };

	
	public static boolean isFaceAdjacent(Face f1, Face f2) {
		if (f1 == f2)
			return false;

		switch (f1) {
		case FRONT:
			return (f2 != Face.BACK);
		case BACK:
			return (f2 != Face.FRONT);
		case LEFT:
			return (f2 != Face.RIGHT);
		case RIGHT:
			return (f2 != Face.LEFT);
		case TOP:
			return (f2 != Face.BOTTOM);
		case BOTTOM:
			return (f2 != Face.BOTTOM);
		}

		return false;
	}
	
	public static Move[] getReverseSequence(List<Move> moves) {
		Move[] results = new Move[moves.size()];
		int index = moves.size()-1;
		for (Move m : moves) {
			results[index--] = new Move(m.getFace(), (-1 * m.getAngle()));
		}

		return results;
	}

	public static PieceState getPieceState(RubiksCube cube, Point3D location) {
		Piece p = cube.getPieceByLocation(location);
		return new PieceState(p, location);
	}

	public static PieceState getPieceIDState(RubiksCube cube, Point3D location) {
		Piece p = cube.getPieceByLocation(location);
		return new PieceState(p.getID(), location);
	}
	
	public static int getTargetStateID(RubiksCube cube, Point3D location) {
		int id = 0;
		
		if (location.x == -1) {
			id += cube.getColorByFace(Face.LEFT).bitmask;
		}
		if (location.x == 1) {
			id += cube.getColorByFace(Face.RIGHT).bitmask;
		}
		if (location.y == -1) {
			id += cube.getColorByFace(Face.BOTTOM).bitmask;
		}
		if (location.y == 1) {
			id += cube.getColorByFace(Face.TOP).bitmask;
		}
		if (location.z == -1) {
			id += cube.getColorByFace(Face.BACK).bitmask;
		}
		if (location.z == 1) {
			id += cube.getColorByFace(Face.FRONT).bitmask;
		}

		return id;
	}
	
	public static boolean isMoveWasted(List<Move> previousMoves, Move nextMove) {
		int duplicates = 0;

		for (int i = previousMoves.size() - 1; i >= 0; i--) {
			Move prevMove = previousMoves.get(i);

			if (RuleHelper.isFaceAdjacent(nextMove.getFace(), prevMove.getFace())) {
				// previous move was on an adjacent face so this move can't be wasted.
				return false;
			}

			if (nextMove.getFace() == prevMove.getFace()) {
				if (nextMove.getAngle() + prevMove.getAngle() == 0) {
					// This move is undoing the previous move.
					return true;
				}

				if (prevMove.equals(nextMove)) {
					// Three identical moves on the same face with no moves on adjacent faces
					// between is a waste.
					duplicates++;
					if (duplicates == 2) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
