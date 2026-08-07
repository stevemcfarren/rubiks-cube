package com.stevemcfarren.rubikscube;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.stevemcfarren.rubikscube.RubiksCube.Face;

public class TestHelper {
	public final static Color front = RubiksCubeManager.getNormalizedColor(Face.FRONT);
	public final static Color back = RubiksCubeManager.getNormalizedColor(Face.BACK);
	public final static Color top = RubiksCubeManager.getNormalizedColor(Face.TOP);
	public final static Color bottom = RubiksCubeManager.getNormalizedColor(Face.BOTTOM);
	public final static Color right = RubiksCubeManager.getNormalizedColor(Face.RIGHT);
	public final static Color left = RubiksCubeManager.getNormalizedColor(Face.LEFT);

	private TestHelper() {

	}
	
	public static void assertPieceSolved(RubiksCube cube, Point3D location) {
		Piece p = cube.getPieceByLocation(location);

		if (location.x == -1) {
			assertEquals(cube.getColorByFace(Face.LEFT), p.getXColor());
		}
		else if (location.x == 1) {
			assertEquals(cube.getColorByFace(Face.RIGHT), p.getXColor());
		}
		else {
			assertEquals(Color.NONE, p.getXColor());
		}
		
		if (location.y == -1) {
			assertEquals(cube.getColorByFace(Face.BOTTOM), p.getYColor());
		}
		else if (location.y == 1) {
			assertEquals(cube.getColorByFace(Face.TOP), p.getYColor());
		}
		else {
			assertEquals(Color.NONE, p.getYColor());
		}

		if (location.z == -1) {
			assertEquals(cube.getColorByFace(Face.BACK), p.getZColor());
		}
		else if (location.z == 1) {
			assertEquals(cube.getColorByFace(Face.FRONT), p.getZColor());
		}
		else {
			assertEquals(Color.NONE, p.getZColor());
		}
	}

	public static void WriteJSON(String filename, String json) {
		Path path = Paths.get("output");
        
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            System.err.println("Failed to create directory: " + e.getMessage());
        }
		try {
			FileWriter writer = new FileWriter("output/" + filename);

			writer.write(json);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Piece[] getSolvedPieces() {
		Color none = Color.NONE;
		Color front = RubiksCubeManager.getNormalizedColor(Face.FRONT);
		Color back = RubiksCubeManager.getNormalizedColor(Face.BACK);
		Color top = RubiksCubeManager.getNormalizedColor(Face.TOP);
		Color bottom = RubiksCubeManager.getNormalizedColor(Face.BOTTOM);
		Color right = RubiksCubeManager.getNormalizedColor(Face.RIGHT);
		Color left = RubiksCubeManager.getNormalizedColor(Face.LEFT);

		// Pieces must be sorted by location in the order X, Y, Z
		Piece[] pieces = { new Piece(left, bottom, back), // (-1,-1,-1)
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

		return pieces;
	}
}
