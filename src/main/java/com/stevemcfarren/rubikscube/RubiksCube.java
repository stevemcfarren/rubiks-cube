package com.stevemcfarren.rubikscube;

import com.stevemcfarren.rubikscube.Move.Direction;
import com.stevemcfarren.rubikscube.Point3D.Axis;

/**
 * Represents a 3x3x3 Rubik's Cube. The cube consists of 26 pieces arranged in a
 * 3D grid.
 */
public class RubiksCube {

	final private Piece[][][] pieceMatrix = new Piece[3][3][3];

	/**
	 * Represents the six faces of the Rubik's Cube.
	 */
	public enum Face {
		FRONT, BACK, LEFT, RIGHT, TOP, BOTTOM
	};

	/**
	 * Face by face visual representation of the cube.
	 */
	public record DisplayData(String[][] top, String[][] bottom, String[][] left, String[][] right, String[][] front,
			String[][] back) {
	}

	/**
	 * Constructs a new RubiksCube in the same state as the given cube.
	 *
	 * @param sourceCube the cube to copy
	 */
	public RubiksCube(RubiksCube sourceCube) {
		for (int x = 0; x <= 2; x++) {
			for (int y = 0; y <= 2; y++) {
				for (int z = 0; z <= 2; z++) {
					if (x == 1 && y == 1 && z == 1) {
						continue;
					}
					pieceMatrix[x][y][z] = new Piece(sourceCube.pieceMatrix[x][y][z]);
				}
			}
		}
	}

	/**
	 * Constructs a new RubiksCube in the state identified by stateIdentifier.
	 *
	 * @param stateIdentifier the initial state of the new cube.
	 * @throws InvalidRubiksCubeState if the given state is invalid.
	 */
	public RubiksCube(String stateIdentifier) {
		String[][][] faces = new String[6][3][3];

		String[] facesIds = stateIdentifier.split("-");
		if (facesIds.length != 6) {
			throw new InvalidRubiksCubeState("Invalid state identifier: " + stateIdentifier);
		}

		for (int faceIndex = 0; faceIndex < 6; faceIndex++) {
			int faceID = Integer.parseInt(facesIds[faceIndex], 36);

			for (int row = 0; row <= 2; row++) {
				for (int col = 0; col <= 2; col++) {
					faces[faceIndex][row][col] = Color.getById(faceID % 6).toString();
					faceID = (int) (faceID / 6);
				}
			}
		}

		this(new DisplayData(faces[0], faces[1], faces[2], faces[3], faces[4], faces[5]));
	}

	/**
	 * Constructs a new RubiksCube matching the given display data.
	 *
	 * @param data the display data specifying the state of the new cube.
	 * @throws InvalidRubiksCubeState if the given state is invalid.
	 */
	public RubiksCube(DisplayData data) {
		Piece[] pieces = new Piece[26];
		try {
			pieces[0] = new Piece(Color.valueOf(data.left[2][0]), Color.valueOf(data.bottom[2][0]),
					Color.valueOf(data.back[2][2])); // (-1,-1,-1)

			pieces[1] = new Piece(Color.valueOf(data.left[2][1]), Color.valueOf(data.bottom[1][0]), Color.NONE); // (-1,-1,0)
			pieces[2] = new Piece(Color.valueOf(data.left[2][2]), Color.valueOf(data.bottom[0][0]),
					Color.valueOf(data.front[2][0])); // (-1,-1,1)

			pieces[3] = new Piece(Color.valueOf(data.left[1][0]), Color.NONE, Color.valueOf(data.back[1][2])); // (-1,0,-1)
			pieces[4] = new Piece(Color.valueOf(data.left[1][1]), Color.NONE, Color.NONE); // (-1,0,0)
			pieces[5] = new Piece(Color.valueOf(data.left[1][2]), Color.NONE, Color.valueOf(data.front[1][0])); // (-1,0,1)

			pieces[6] = new Piece(Color.valueOf(data.left[0][0]), Color.valueOf(data.top[0][0]),
					Color.valueOf(data.back[0][2])); // (-1,1,-1)
			pieces[7] = new Piece(Color.valueOf(data.left[0][1]), Color.valueOf(data.top[1][0]), Color.NONE); // (-1,1,0)
			pieces[8] = new Piece(Color.valueOf(data.left[0][2]), Color.valueOf(data.top[2][0]),
					Color.valueOf(data.front[0][0])); // (-1,1,1)

			pieces[9] = new Piece(Color.NONE, Color.valueOf(data.bottom[2][1]), Color.valueOf(data.back[2][1])); // (0,-1,-1)
			pieces[10] = new Piece(Color.NONE, Color.valueOf(data.bottom[1][1]), Color.NONE); // (0,-1,0)
			pieces[11] = new Piece(Color.NONE, Color.valueOf(data.bottom[0][1]), Color.valueOf(data.front[2][1])); // (0,-1,1)

			pieces[12] = new Piece(Color.NONE, Color.NONE, Color.valueOf(data.back[1][1])); // (0,0,-1)
			// No piece at (0, 0, 0)
			pieces[13] = new Piece(Color.NONE, Color.NONE, Color.valueOf(data.front[1][1])); // (0,0,1)

			pieces[14] = new Piece(Color.NONE, Color.valueOf(data.top[0][1]), Color.valueOf(data.back[0][1])); // (0,1,-1)
			pieces[15] = new Piece(Color.NONE, Color.valueOf(data.top[1][1]), Color.NONE); // (0,1,0)
			pieces[16] = new Piece(Color.NONE, Color.valueOf(data.top[2][1]), Color.valueOf(data.front[0][1])); // (0,1,1)

			pieces[17] = new Piece(Color.valueOf(data.right[2][2]), Color.valueOf(data.bottom[2][2]),
					Color.valueOf(data.back[2][0])); // (1,-1,-1)
			pieces[18] = new Piece(Color.valueOf(data.right[2][1]), Color.valueOf(data.bottom[1][2]), Color.NONE); // (1,-1,0)
			pieces[19] = new Piece(Color.valueOf(data.right[2][0]), Color.valueOf(data.bottom[0][2]),
					Color.valueOf(data.front[2][2])); // (1,-1,1)

			pieces[20] = new Piece(Color.valueOf(data.right[1][2]), Color.NONE, Color.valueOf(data.back[1][0])); // (1,0,-1)
			pieces[21] = new Piece(Color.valueOf(data.right[1][1]), Color.NONE, Color.NONE); // (1,0,0)
			pieces[22] = new Piece(Color.valueOf(data.right[1][0]), Color.NONE, Color.valueOf(data.front[1][2])); // (1,0,1)

			pieces[23] = new Piece(Color.valueOf(data.right[0][2]), Color.valueOf(data.top[0][2]),
					Color.valueOf(data.back[0][0])); // (1,1,-1)
			pieces[24] = new Piece(Color.valueOf(data.right[0][1]), Color.valueOf(data.top[1][2]), Color.NONE); // (1,1,0)
			pieces[25] = new Piece(Color.valueOf(data.right[0][0]), Color.valueOf(data.top[2][2]),
					Color.valueOf(data.front[0][2])); // (1,1,1)

		} catch (IllegalArgumentException e) {
			throw new InvalidRubiksCubeState(e);
		}

		this(pieces);
	}

	/**
	 * Constructs a new RubiksCube with the given pieces.
	 *
	 * @param pieces used to construct the new cube.
	 * @throws InvalidRubiksCubeState if the given pieces are invalid.
	 */
	protected RubiksCube(Piece[] pieces) {
		if (pieces.length != 26) {
			throw new InvalidRubiksCubeState("Cube must contain 26 pieces. Given pieces = " + pieces.length);
		}

		int next = 0;
		boolean[] duplicateCheck = new boolean[56];

		for (int x = 0; x <= 2; x++) {
			for (int y = 0; y <= 2; y++) {
				for (int z = 0; z <= 2; z++) {
					if (x == 1 && y == 1 && z == 1) {
						continue;
					}

					Piece p = pieces[next];

					if (p == null) {
						throw new InvalidRubiksCubeState("Given pieces must not be null.");
					}

					// If we already found a piece with the same ID, fail. Otherwise mark this ID as
					// found.
					if (duplicateCheck[p.getID() - 1])
						throw new InvalidRubiksCubeState("Duplicate piece found with ID = " + p.getID());
					else
						duplicateCheck[p.getID() - 1] = true;

					// Validate we have the valid colors based on position (for example, middle row
					// pieces can't have Z color).
					if ((x - 1 == 0 && p.getXColor() != Color.NONE) || (x - 1 != 0 && p.getXColor() == Color.NONE)) {
						throw new InvalidRubiksCubeState(String.format("Illegal 'X' color %s at (%d, %d, %d)",
								p.getXColor(), x - 1, y - 1, z - 1));
					}
					if ((y - 1 == 0 && p.getYColor() != Color.NONE) || (y - 1 != 0 && p.getYColor() == Color.NONE)) {
						throw new InvalidRubiksCubeState(String.format("Illegal 'Y' color %s at (%d, %d, %d)",
								p.getYColor(), x - 1, y - 1, z - 1));
					}
					if ((z - 1 == 0 && p.getZColor() != Color.NONE) || (z - 1 != 0 && p.getZColor() == Color.NONE)) {
						throw new InvalidRubiksCubeState(String.format("Illegal 'Z' color %s at (%d, %d, %d)",
								p.getZColor(), x - 1, y - 1, z - 1));
					}

					pieceMatrix[x][y][z] = p;
					next++;
				}
			}
		}
	}

	/**
	 * Creates a copy of all 26 pieces in the cube. TODO: Consider removing this
	 * function; not sure it has value beyond testing and exposes internal
	 * implementation details.
	 *
	 * @return an array of 26 Piece objects
	 */
	public Piece[] copyPieces() {
		Piece[] copyPieces = new Piece[26];
		int count = 0;
		for (int x = 0; x <= 2; x++) {
			for (int y = 0; y <= 2; y++) {
				for (int z = 0; z <= 2; z++) {
					if (!(x == 1 && y == 1 && z == 1)) {
						copyPieces[count] = new Piece(this.pieceMatrix[x][y][z]);
						count++;
					}
				}
			}
		}
		return copyPieces;
	}

	private Piece swapPiece(Point3D loc, Piece newPiece) {
		Piece temp = this.getPieceByLocation(loc);
		pieceMatrix[loc.x + 1][loc.y + 1][loc.z + 1] = newPiece;
		return temp;
	}

	private void rotatePieces(Point3D firstLoc, Axis axis, int angle) {

		if (angle == 180 || angle == -180) {
			angle = 90;
			this.rotatePieces(firstLoc, axis, 90);
		}

		Piece piece = this.getPieceByLocation(firstLoc);
		Point3D nextLoc = firstLoc.rotate(angle, axis);

		for (int i = 0; i < 4; i++) {
			piece.rotate(axis, angle);
			piece = swapPiece(nextLoc, piece);
			nextLoc = nextLoc.rotate(angle, axis);
		}
	}

	/**
	 * Rotate the specified face of the cube.
	 * 
	 * @param face      the face to be rotated
	 * @param direction to Rotate
	 */
	public void rotateFace(Face face, Direction direction) {
		int angle = direction == Direction.CLOCKWISE ? 90 : -90;

		Axis axis = null;
		int axisValue = -1;

		switch (face) {
		case RIGHT:
			axisValue = 1;
			angle *= -1;
		case LEFT:
			angle *= -1;
			axis = Axis.X;
			// Rotate the four corners
			this.rotatePieces(new Point3D(axisValue, 1, 1), axis, angle);
			// Rotate the four edges
			this.rotatePieces(new Point3D(axisValue, 0, 1), axis, angle);
			break;
		case TOP:
			axisValue = 1;
			angle *= -1;
		case BOTTOM:
			axis = Axis.Y;
			// Rotate the four corners
			this.rotatePieces(new Point3D(1, axisValue, 1), axis, angle);
			// Rotate the four edges
			this.rotatePieces(new Point3D(0, axisValue, 1), axis, angle);
			break;
		case FRONT:
			axisValue = 1;
			angle *= -1;
		case BACK:
			axis = Axis.Z;
			// Rotate the four corners
			this.rotatePieces(new Point3D(1, 1, axisValue), axis, angle);
			// Rotate the four edges
			this.rotatePieces(new Point3D(0, 1, axisValue), axis, angle);
			break;
		}
	}

	/**
	 * Rotate the entire cube in the given direction from user's perspective
	 * (looking at specified face).
	 * 
	 * @param face      perspective from which to rotate the cube
	 * @param direction to Rotate the cube
	 */
	public void rotateCube(Face face, Direction direction) {
		int angle = direction == Direction.CLOCKWISE ? 90 : -90;

		Axis axis = null;

		switch (face) {
		case LEFT:
			angle *= -1;
		case RIGHT:
			axis = Axis.X;
			this.rotatePieces(new Point3D(-1, 1, 1), axis, angle);
			this.rotatePieces(new Point3D(-1, 0, 1), axis, angle);
			this.rotatePieces(new Point3D(0, 0, 1), axis, angle);
			this.rotatePieces(new Point3D(0, 1, 1), axis, angle);
			this.rotatePieces(new Point3D(1, 1, 1), axis, angle);
			this.rotatePieces(new Point3D(1, 0, 1), axis, angle);
			break;
		case TOP:
			angle *= -1;
		case BOTTOM:
			axis = Axis.Y;
			this.rotatePieces(new Point3D(1, -1, 1), axis, angle);
			this.rotatePieces(new Point3D(0, -1, 1), axis, angle);
			this.rotatePieces(new Point3D(1, 0, 1), axis, angle);
			this.rotatePieces(new Point3D(0, 0, 1), axis, angle);
			this.rotatePieces(new Point3D(1, 1, 1), axis, angle);
			this.rotatePieces(new Point3D(0, 1, 1), axis, angle);
			break;
		case FRONT:
			angle *= -1;
		case BACK:
			axis = Axis.Z;
			this.rotatePieces(new Point3D(1, 1, -1), axis, angle);
			this.rotatePieces(new Point3D(0, 1, -1), axis, angle);
			this.rotatePieces(new Point3D(1, 1, 0), axis, angle);
			this.rotatePieces(new Point3D(0, 1, 0), axis, angle);
			this.rotatePieces(new Point3D(1, 1, 1), axis, angle);
			this.rotatePieces(new Point3D(0, 1, 1), axis, angle);
			break;
		}
	}

	/**
	 * Checks if the entire cube is in a solved state. A cube is solved if every
	 * sticker on each face has the same color.
	 *
	 * @return true if the cube is solved, false otherwise
	 */
	public boolean isCubeSolved() {
		// Get colors of center pieces for each face.
		Color front = getPieceByLocation(new Point3D(0, 0, 1)).getZColor();
		Color back = getPieceByLocation(new Point3D(0, 0, -1)).getZColor();
		Color top = getPieceByLocation(new Point3D(0, 1, 0)).getYColor();
		Color bottom = getPieceByLocation(new Point3D(0, -1, 0)).getYColor();
		Color right = getPieceByLocation(new Point3D(1, 0, 0)).getXColor();
		Color left = getPieceByLocation(new Point3D(-1, 0, 0)).getXColor();

		// Ensure each pieces visible faces match the center color for that face.
		for (int x = 0; x <= 2; x++) {
			for (int y = 0; y <= 2; y++) {
				for (int z = 0; z <= 2; z++) {
					if (x == 1 && y == 1 && z == 1) {
						continue;
					}
					Piece p = pieceMatrix[x][y][z];
					if ((z - 1 == 1) && (p.getZColor() != front))
						return false;
					if ((z - 1 == -1) && (p.getZColor() != back))
						return false;
					if ((y - 1 == 1) && (p.getYColor() != top))
						return false;
					if ((y - 1 == -1) && (p.getYColor() != bottom))
						return false;
					if ((x - 1 == 1) && (p.getXColor() != right))
						return false;
					if ((x - 1 == -1) && (p.getXColor() != left))
						return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the piece at the specific location is in its correct solved
	 * position and orientation.
	 *
	 * @param l the location of the piece to check
	 * @return true if the piece is solved, false otherwise
	 */
	public boolean isPieceSolved(Point3D l) {
		Piece p = getPieceByLocation(l);

		if (l.x == -1) {
			if (p.getXColor() != getColorByFace(Face.LEFT)) {
				return false;
			}
		}
		if (l.x == 1) {
			if (p.getXColor() != getColorByFace(Face.RIGHT)) {
				return false;
			}
		}

		if (l.y == -1) {
			if (p.getYColor() != getColorByFace(Face.BOTTOM)) {
				return false;
			}
		}
		if (l.y == 1) {
			if (p.getYColor() != getColorByFace(Face.TOP)) {
				return false;
			}
		}

		if (l.z == -1) {
			if (p.getZColor() != getColorByFace(Face.BACK)) {
				return false;
			}
		}
		if (l.z == 1) {
			if (p.getZColor() != getColorByFace(Face.FRONT)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if the piece at the specific location is in its correct solved
	 * position, ignoring orientation.
	 *
	 * @param l the location of the piece to check
	 * @return true if the piece is in place, false otherwise
	 */
	public boolean isPieceInPlace(Point3D l) {
		int id = 0;

		if (l.x == -1) {
			id += getColorByFace(Face.LEFT).bitmask;
		}
		if (l.x == 1) {
			id += getColorByFace(Face.RIGHT).bitmask;
		}
		if (l.y == -1) {
			id += getColorByFace(Face.BOTTOM).bitmask;
		}
		if (l.y == 1) {
			id += getColorByFace(Face.TOP).bitmask;
		}
		if (l.z == -1) {
			id += getColorByFace(Face.BACK).bitmask;
		}
		if (l.z == 1) {
			id += getColorByFace(Face.FRONT).bitmask;
		}

		return id == getPieceByLocation(l).getID();
	}

	/**
	 * Retrieves the piece at the specified 3D location.
	 *
	 * @param location the 3D coordinates (x, y, z) where each coordinate is in the
	 *                 range [-1, 1]
	 * @return the Piece at the specified location
	 * @throws IllegalArgumentException if any coordinate is out of the [-1, 1]
	 *                                  range
	 */
	public Piece getPieceByLocation(Point3D location) {
		if (location.x < -1 || location.x > 1) {
			throw new IllegalArgumentException("X not in range [-1..1]: " + location.x);
		}

		if (location.y < -1 || location.y > 1) {
			throw new IllegalArgumentException("Y not in range [-1..1]: " + location.y);
		}

		if (location.z < -1 || location.z > 1) {
			throw new IllegalArgumentException("Z not in range [-1..1]: " + location.z);
		}

		return pieceMatrix[location.x + 1][location.y + 1][location.z + 1];
	}

	/**
	 * Finds the current location of a piece with the specified colors.
	 *
	 * @param c1 the first color
	 * @param c2 the second color
	 * @param c3 the third color (use Color.NONE for edges or centers)
	 * @return the Point3D location of the piece, or null if not found
	 */
	public Point3D findPieceByColor(Color c1, Color c2, Color c3) {
		int id = c1.bitmask + c2.bitmask + c3.bitmask;

		for (int x = 0; x <= 2; x++) {
			for (int y = 0; y <= 2; y++) {
				for (int z = 0; z <= 2; z++) {

					if (x == 1 && y == 1 && z == 1) {
						continue;
					}

					if (id == pieceMatrix[x][y][z].getID()) {
						return new Point3D(x - 1, y - 1, z - 1);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Identifies which face currently has the specified color on its center piece.
	 *
	 * @param c the color to look for
	 * @return the Face with the specified center color, or null if not found
	 */
	public Face getFaceByColor(Color c) {
		for (Face f : Face.values()) {
			if (c == this.getColorByFace(f)) {
				return f;
			}
		}

		return null;
	}

	/**
	 * Retrieves the color of the center piece of the specified face.
	 *
	 * @param f the face to check
	 * @return the Color of the center piece of that face, or null if the face is
	 *         null
	 */
	public Color getColorByFace(Face f) {
		if (f == null)
			return null;

		switch (f) {
		case TOP:
			return pieceMatrix[1][2][1].getYColor();
		case BOTTOM:
			return pieceMatrix[1][0][1].getYColor();
		case LEFT:
			return pieceMatrix[0][1][1].getXColor();
		case RIGHT:
			return pieceMatrix[2][1][1].getXColor();
		case FRONT:
			return pieceMatrix[1][1][2].getZColor();
		case BACK:
			return pieceMatrix[1][1][0].getZColor();
		}
		return null;
	}

	/**
	 * Returns a record representing all faces and their sticker colors. Each face
	 * is represented as a 3x3 array of color names.
	 *
	 * @return a DisplayData record containing 3x3 String arrays with the sticker
	 *         colors for each face.
	 */
	public DisplayData getDisplayData() {
		String[][] top = new String[3][3];
		String[][] bottom = new String[3][3];
		String[][] left = new String[3][3];
		String[][] right = new String[3][3];
		String[][] front = new String[3][3];
		String[][] back = new String[3][3];

		for (int row = 0; row <= 2; row++) {
			for (int col = 0; col <= 2; col++) {

				int revRow = ((row - 1) * -1) + 1;
				int revCol = ((col - 1) * -1) + 1;

				left[row][col] = pieceMatrix[0][revRow][col].getXColor().toString();
				right[row][col] = pieceMatrix[2][revRow][revCol].getXColor().toString();

				top[row][col] = pieceMatrix[col][2][row].getYColor().toString();
				bottom[row][col] = pieceMatrix[col][0][revRow].getYColor().toString();

				back[row][col] = pieceMatrix[revCol][revRow][0].getZColor().toString();
				front[row][col] = pieceMatrix[col][revRow][2].getZColor().toString();
			}
		}

		return new DisplayData(top, bottom, left, right, front, back);
	}

	/**
	 * Generates and returns a unique identifier for the cube's current state.
	 *
	 * @return a String identifying the cube's current state.
	 */
	public String getStateIdentifier() {
		int top = 0;
		int bottom = 0;
		int left = 0;
		int right = 0;
		int front = 0;
		int back = 0;

		// Construct a base six number with the right-most digit representing the color
		// of the top-left sticker on each face.
		for (int row = 0; row <= 2; row++) {
			for (int col = 0; col <= 2; col++) {

				int revRow = ((row - 1) * -1) + 1;
				int revCol = ((col - 1) * -1) + 1;

				int position = Math.powExact(6, (row * 3 + col));

				left += pieceMatrix[0][revRow][col].getXColor().id * position;
				right += pieceMatrix[2][revRow][revCol].getXColor().id * position;

				top += pieceMatrix[col][2][row].getYColor().id * position;
				bottom += pieceMatrix[col][0][revRow].getYColor().id * position;

				back += pieceMatrix[revCol][revRow][0].getZColor().id * position;
				front += pieceMatrix[col][revRow][2].getZColor().id * position;
			}
		}

		return Integer.toString(top, 36) + '-' + Integer.toString(bottom, 36) + '-' + Integer.toString(left, 36) + '-'
				+ Integer.toString(right, 36) + '-' + Integer.toString(front, 36) + '-' + Integer.toString(back, 36);
	}
}
