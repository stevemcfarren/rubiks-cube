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
}
