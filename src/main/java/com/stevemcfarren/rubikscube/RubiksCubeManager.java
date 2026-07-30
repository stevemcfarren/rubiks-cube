package com.stevemcfarren.rubikscube;

import com.stevemcfarren.rubikscube.RubiksCube.Face;

/**
 * Provides factory methods for creating Rubik's Cube instances.
 * Handles cube initialization, normalization, and randomization.
 * 
 * @author Steve McFarren
 */
public class RubiksCubeManager {

	/**
	 * Constructs a Rubik's Cube in a randomized but solvable state.
	 * The cube is created by starting with a solved cube and applying random moves.
	 * 
	 * @return A RubiksCube in a random, solvable state.
	 */
	public static RubiksCube getRandomCube() {
		RubiksCube cube = RubiksCubeManager.getSolvedCube();

		final Move[] ALLMOVES = { new Move(Face.FRONT, 90), new Move(Face.FRONT, -90),
				new Move(Face.BACK, 90), new Move(Face.BACK, -90), new Move(Face.LEFT, 90),
				new Move(Face.LEFT, -90), new Move(Face.RIGHT, 90), new Move(Face.RIGHT, -90),
				new Move(Face.TOP, 90), new Move(Face.TOP, -90), new Move(Face.BOTTOM, 90),
				new Move(Face.BOTTOM, -90) };

		for (int i = 0; i < 30; i++) {
			int index = (int) (Math.random() * ALLMOVES.length);
			cube.rotateFace(ALLMOVES[index].getFace(), ALLMOVES[index].getAngle());
		}

		return cube;
	}

	/**
	 * Construct a solved Rubik's Cube.
	 * 
	 * @return A RubiksCube in the solved state.
	 */
	public static RubiksCube getSolvedCube() {
		Color none = Color.NONE;
		Color front = getNormalizedColor(Face.FRONT);
		Color back = getNormalizedColor(Face.BACK);
		Color top = getNormalizedColor(Face.TOP);
		Color bottom = getNormalizedColor(Face.BOTTOM);
		Color right = getNormalizedColor(Face.RIGHT);
		Color left = getNormalizedColor(Face.LEFT);

		// Pieces must be sorted by location in the order X, Y, Z
		Piece[] pieces = { 
			new Piece(left, bottom, back), // (-1,-1,-1)
			new Piece(left, bottom, none), // (-1,-1,0)
			new Piece(left, bottom, front), // (-1,-1,1)

			new Piece(left, none, back), // (-1,0,-1)
			new Piece(left, none, none), // (-1,0,0)
			new Piece(left, none, front), // (-1,0,1)
			
			new Piece(left, top, back), // (-1,1,-1)
			new Piece(left, top, none), // (-1,1,0)
			new Piece(left, top, front), // (-1,1,1)

			new Piece(none, bottom, back), // (0,-1,-1)
			new Piece(none, bottom, none), // (0,-1,0)
			new Piece(none, bottom, front), // (0,-1,1)

			new Piece(none, none, back), // (0,0,-1)
			// No piece at (0, 0, 0)
			new Piece(none, none, front), // (0,0,1)

			new Piece(none, top, back), // (0,1,-1)
			new Piece(none, top, none), // (0,1,0)
			new Piece(none, top, front), // (0,1,1)

			new Piece(right, bottom, back), // (1,-1,-1)
			new Piece(right, bottom, none), // (1,-1,0)
			new Piece(right, bottom, front), // (1,-1,1)

			new Piece(right, none, back), // (1,0,-1)
			new Piece(right, none, none), // (1,0,0)
			new Piece(right, none, front), // (1,0,1)

			new Piece(right, top, back), // (1,1,-1)
			new Piece(right, top, none), // (1,1,0)
			new Piece(right, top, front), // (1,1,1)
		};

		return new RubiksCube(pieces);
	}

	/** 
	 * Define the "normalized" orientation of the cube.  Which color is chosen for top, front, etc is not
	 * important, but it is important to have a normalized orientation to make it easier to define rules.
	 * 
	 * White was chosen as top and blue as front simply because the auther's Rubik's Cube has a logo on the
	 * white center piece that looks best in this orientation.
	 * 
	 * @param f the Face to find the color for.
	 * @return the Color of the given face.
	 */
	public static Color getNormalizedColor(Face f) {
		switch (f) {
		case FRONT:
			return Color.BLUE;
		case BACK:
			return Color.GREEN;
		case TOP:
			return Color.WHITE;
		case BOTTOM:
			return Color.YELLOW;
		case LEFT:
			return Color.RED;
		case RIGHT:
			return Color.ORANGE;
		}
		return Color.NONE;
	}

}
