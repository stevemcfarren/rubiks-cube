package com.stevemcfarren.rubikscube;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.stevemcfarren.rubikscube.RubiksCube.DisplayData;
import com.stevemcfarren.rubikscube.RubiksCube.Face;

class RubiksCubeTest {
	static List<TestResult> testResults;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		testResults = new ArrayList<TestResult>();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		Gson gson = new Gson();
		String json = gson.toJson(testResults);
		TestHelper.WriteJSON("RubiksCubeTestOutput.json", json);
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	final void testRubiksCube() {
		RubiksCube cube = null;

		try {
			cube = new RubiksCube(new Piece[1]);
			fail("Expected InvalidRubiksCubeState");
		} catch (InvalidRubiksCubeState e) {
			assertEquals("Cube must contain 26 pieces. Given pieces = 1", e.getMessage());
		}

		try {
			cube = new RubiksCube(new Piece[26]);
			fail("Expected InvalidRubiksCubeState");
		} catch (InvalidRubiksCubeState e) {
			assertEquals("Given pieces must not be null.", e.getMessage());
		}

		cube = RubiksCubeManager.getSolvedCube();
		assertTrue(cube.isCubeSolved(), "Initial state should be solved.");

		Piece pieces[] = cube.copyPieces();

		try {
			pieces[0] = new Piece(Color.NONE, Color.YELLOW, Color.GREEN);
			cube = new RubiksCube(pieces);
			fail("Expected InvalidRubiksCubeState");
		} catch (InvalidRubiksCubeState e) {
			assertEquals("Illegal 'X' color NONE at (-1, -1, -1)", e.getMessage());
		}
		try {
			pieces[0] = new Piece(Color.RED, Color.NONE, Color.GREEN);
			cube = new RubiksCube(pieces);
			fail("Expected InvalidRubiksCubeState");
		} catch (InvalidRubiksCubeState e) {
			assertEquals("Illegal 'Y' color NONE at (-1, -1, -1)", e.getMessage());
		}
		try {
			pieces[0] = new Piece(Color.RED, Color.YELLOW, Color.NONE);
			cube = new RubiksCube(pieces);
			fail("Expected InvalidRubiksCubeState");
		} catch (InvalidRubiksCubeState e) {
			assertEquals("Illegal 'Z' color NONE at (-1, -1, -1)", e.getMessage());
		}

		try {
			pieces[0] = new Piece(Color.RED, Color.YELLOW, Color.BLUE);
			cube = new RubiksCube(pieces);
			fail("Expected InvalidRubiksCubeState");
		} catch (InvalidRubiksCubeState e) {
			assertEquals("Duplicate piece found with ID = 38", e.getMessage());
		}

		pieces[0] = new Piece(Color.RED, Color.YELLOW, Color.GREEN);

		try {
			pieces[11] = new Piece(Color.ORANGE, Color.YELLOW, Color.BLUE);
			cube = new RubiksCube(pieces);
			fail("Expected InvalidRubiksCubeState");
		} catch (InvalidRubiksCubeState e) {
			assertEquals("Illegal 'X' color ORANGE at (0, -1, 1)", e.getMessage());
		}
		try {
			pieces[3] = new Piece(Color.RED, Color.BLUE, Color.GREEN);
			cube = new RubiksCube(pieces);
			fail("Expected InvalidRubiksCubeState");
		} catch (InvalidRubiksCubeState e) {
			assertEquals("Illegal 'Y' color BLUE at (-1, 0, -1)", e.getMessage());
		}
		try {
			pieces[1] = new Piece(Color.RED, Color.YELLOW, Color.BLUE);
			cube = new RubiksCube(pieces);
			fail("Expected InvalidRubiksCubeState");
		} catch (InvalidRubiksCubeState e) {
			assertEquals("Illegal 'Z' color BLUE at (-1, -1, 0)", e.getMessage());
		}

	}

	@Test
	final void testRubiksCube_Copy() {
		RubiksCube cube1 = RubiksCubeManager.getSolvedCube();
		RubiksCube cube2 = new RubiksCube(cube1);
		assertTrue(cube1.isCubeSolved(), "Cube1 should initially be solved.");
		assertTrue(cube2.isCubeSolved(), "Cube2 should initially be solved.");
		cube1.rotateFace(Face.FRONT, 90);
		assertFalse(cube1.isCubeSolved(), "Cube1 should no longer be solved.");
		assertTrue(cube2.isCubeSolved(), "Cube2 should still be solved.");
	}

	@Test
	final void testRubiksCube_RotateFace() {
		RubiksCube cube = RubiksCubeManager.getSolvedCube();
		assertTrue(cube.isCubeSolved(), "Initial state should be solved.");

		cube.rotateFace(Face.FRONT, 0);
		assertTrue(cube.isCubeSolved(), "Cube should be solved after rotation of zero.");

		try {
			cube.rotateFace(Face.FRONT, -360);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Angle must be a multiple of 90 between -90 and 270: -360", e.getMessage());
		}
		try {
			cube.rotateFace(Face.FRONT, 360);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Angle must be a multiple of 90 between -90 and 270: 360", e.getMessage());
		}
		try {
			cube.rotateFace(Face.FRONT, 45);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Angle must be a multiple of 90 between -90 and 270: 45", e.getMessage());
		}

		for (Face f : Face.values()) {
			cube.rotateFace(f, 90);
			assertTrue(!cube.isCubeSolved(), "Cube should no longer be solved.");
			cube.rotateFace(f, 270);
			assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating face clockwise and back:" + f);
		}

		for (Face f : Face.values()) {
			cube.rotateFace(f, 180);
			assertTrue(!cube.isCubeSolved(), "Cube should no longer be solved.");
			cube.rotateFace(f, 180);
			assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating face clockwise and back:" + f);
		}

		for (Face f : Face.values()) {
			cube.rotateFace(f, 90);
			cube.rotateFace(f, 90);
			cube.rotateFace(f, 90);
			assertTrue(!cube.isCubeSolved(), "Cube should no longer be solved.");
			cube.rotateFace(f, 90);
			assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating face 360 degrees: " + f);
		}
	}

	@Test
	final void testRubiksCube_RotateCube90() {
		RubiksCube cube = RubiksCubeManager.getSolvedCube();

		cube.rotateCube(Face.FRONT, 0);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.FRONT), cube.getColorByFace(Face.FRONT));

		try {
			cube.rotateCube(Face.FRONT, -360);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Angle must be a multiple of 90 between -90 and 270: -360", e.getMessage());
		}
		try {
			cube.rotateCube(Face.FRONT, 360);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Angle must be a multiple of 90 between -90 and 270: 360", e.getMessage());
		}
		try {
			cube.rotateCube(Face.FRONT, 45);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Angle must be a multiple of 90 between -90 and 270: 45", e.getMessage());
		}

		cube.rotateCube(Face.TOP, 90);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.RIGHT), cube.getColorByFace(Face.FRONT));
		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
		cube.rotateCube(Face.TOP, 270);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.FRONT), cube.getColorByFace(Face.FRONT));

		cube.rotateCube(Face.BOTTOM, 90);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.LEFT), cube.getColorByFace(Face.FRONT));
		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
		cube.rotateCube(Face.BOTTOM, -90);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.FRONT), cube.getColorByFace(Face.FRONT));

		cube.rotateCube(Face.RIGHT, 90);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.BOTTOM), cube.getColorByFace(Face.FRONT));
		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
		cube.rotateCube(Face.RIGHT, -90);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.FRONT), cube.getColorByFace(Face.FRONT));

		cube.rotateCube(Face.LEFT, 90);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.TOP), cube.getColorByFace(Face.FRONT));
		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
		cube.rotateCube(Face.LEFT, -90);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.FRONT), cube.getColorByFace(Face.FRONT));

		cube.rotateCube(Face.FRONT, 90);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.LEFT), cube.getColorByFace(Face.TOP));
		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
		cube.rotateCube(Face.FRONT, -90);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.TOP), cube.getColorByFace(Face.TOP));

		cube.rotateCube(Face.BACK, 90);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.RIGHT), cube.getColorByFace(Face.TOP));
		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
		cube.rotateCube(Face.BACK, -90);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.TOP), cube.getColorByFace(Face.TOP));

		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
	}

	@Test
	final void testRubiksCube_RotateCube180() {
		RubiksCube cube = RubiksCubeManager.getSolvedCube();

		cube.rotateCube(Face.TOP, 180);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.BACK), cube.getColorByFace(Face.FRONT));
		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
		cube.rotateCube(Face.TOP, 180);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.FRONT), cube.getColorByFace(Face.FRONT));

		cube.rotateCube(Face.BOTTOM, 180);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.BACK), cube.getColorByFace(Face.FRONT));
		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
		cube.rotateCube(Face.BOTTOM, 180);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.FRONT), cube.getColorByFace(Face.FRONT));

		cube.rotateCube(Face.RIGHT, 180);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.BACK), cube.getColorByFace(Face.FRONT));
		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
		cube.rotateCube(Face.RIGHT, 180);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.FRONT), cube.getColorByFace(Face.FRONT));

		cube.rotateCube(Face.LEFT, 180);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.BACK), cube.getColorByFace(Face.FRONT));
		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
		cube.rotateCube(Face.LEFT, 180);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.FRONT), cube.getColorByFace(Face.FRONT));

		cube.rotateCube(Face.FRONT, 180);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.BOTTOM), cube.getColorByFace(Face.TOP));
		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
		cube.rotateCube(Face.FRONT, 180);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.TOP), cube.getColorByFace(Face.TOP));

		cube.rotateCube(Face.BACK, 180);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.BOTTOM), cube.getColorByFace(Face.TOP));
		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
		cube.rotateCube(Face.BACK, 180);
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.TOP), cube.getColorByFace(Face.TOP));

		assertTrue(cube.isCubeSolved(), "Cube should still be solved after rotating whole cube");
	}

	@Test
	final void testRubiksCube_GetPieceByLocation() {
		RubiksCube cube = RubiksCubeManager.getSolvedCube();

		try {
			cube.getPieceByLocation(new Point3D(-2, 0, 0));
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("X not in range [-1..1]: -2", e.getMessage());
		}
		try {
			cube.getPieceByLocation(new Point3D(2, 0, 0));
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("X not in range [-1..1]: 2", e.getMessage());
		}
		try {
			cube.getPieceByLocation(new Point3D(0, -2, 0));
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Y not in range [-1..1]: -2", e.getMessage());
		}
		try {
			cube.getPieceByLocation(new Point3D(0, 2, 0));
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Y not in range [-1..1]: 2", e.getMessage());
		}
		try {
			cube.getPieceByLocation(new Point3D(0, 0, -2));
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Z not in range [-1..1]: -2", e.getMessage());
		}
		try {
			cube.getPieceByLocation(new Point3D(0, 0, 2));
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Z not in range [-1..1]: 2", e.getMessage());
		}

		Piece p = cube.getPieceByLocation(new Point3D(0, 0, 0));
		assertEquals(null, p);

		p = cube.getPieceByLocation(new Point3D(1, 1, 1));
		assertEquals(new Piece(RubiksCubeManager.getNormalizedColor(Face.RIGHT),
				RubiksCubeManager.getNormalizedColor(Face.TOP), RubiksCubeManager.getNormalizedColor(Face.FRONT)), p);
	}

	@Test
	final void testRubiksCube_FindPieceByColor() {
		RubiksCube cube = RubiksCubeManager.getSolvedCube();

		Point3D pt = cube.findPieceByColor(Color.NONE, Color.NONE, Color.NONE);
		assertEquals(null, pt);

		pt = cube.findPieceByColor(Color.ORANGE, Color.GREEN, Color.YELLOW);
		Piece p = cube.getPieceByLocation(pt);
		assertEquals(56, p.getID());

	}

	@Test
	final void testRubiksCube_GetFaceByColor() {
		RubiksCube cube = RubiksCubeManager.getSolvedCube();

		assertEquals(Face.FRONT, cube.getFaceByColor(RubiksCubeManager.getNormalizedColor(Face.FRONT)));
		assertEquals(Face.RIGHT, cube.getFaceByColor(RubiksCubeManager.getNormalizedColor(Face.RIGHT)));
		assertEquals(Face.BACK, cube.getFaceByColor(RubiksCubeManager.getNormalizedColor(Face.BACK)));
		assertEquals(Face.LEFT, cube.getFaceByColor(RubiksCubeManager.getNormalizedColor(Face.LEFT)));
		assertEquals(Face.TOP, cube.getFaceByColor(RubiksCubeManager.getNormalizedColor(Face.TOP)));
		assertEquals(Face.BOTTOM, cube.getFaceByColor(RubiksCubeManager.getNormalizedColor(Face.BOTTOM)));
		assertEquals(null, cube.getFaceByColor(null));
	}

	@Test
	final void testRubiksCube_GetColorByFace() {
		RubiksCube cube = RubiksCubeManager.getSolvedCube();

		assertEquals(RubiksCubeManager.getNormalizedColor(Face.FRONT), cube.getColorByFace(Face.FRONT));
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.RIGHT), cube.getColorByFace(Face.RIGHT));
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.BACK), cube.getColorByFace(Face.BACK));
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.LEFT), cube.getColorByFace(Face.LEFT));
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.TOP), cube.getColorByFace(Face.TOP));
		assertEquals(RubiksCubeManager.getNormalizedColor(Face.BOTTOM), cube.getColorByFace(Face.BOTTOM));
		assertEquals(null, cube.getColorByFace(null));
	}

	@Test
	final void testRubiksCube_isPieceSolved() {
		Point3D topcorner = new Point3D(1, 1, 1);
		Point3D topedge = new Point3D(0, 1, 1);
		Point3D middleedge = new Point3D(1, 0, 1);
		Point3D bottomcorner = new Point3D(-1, -1, -1);

		RubiksCube cube = RubiksCubeManager.getSolvedCube();
		assertTrue(cube.isPieceSolved(topcorner));
		assertTrue(cube.isPieceSolved(topedge));
		assertTrue(cube.isPieceSolved(middleedge));
		assertTrue(cube.isPieceSolved(bottomcorner));

		cube.rotateFace(Face.RIGHT, 90);
		assertFalse(cube.isPieceSolved(topcorner));
		assertTrue(cube.isPieceSolved(topedge));
		assertFalse(cube.isPieceSolved(middleedge));
		assertTrue(cube.isPieceSolved(bottomcorner));

		cube.rotateFace(Face.TOP, 90);
		assertFalse(cube.isPieceSolved(topcorner));
		assertFalse(cube.isPieceSolved(topedge));
		assertFalse(cube.isPieceSolved(middleedge));
		assertTrue(cube.isPieceSolved(bottomcorner));

		cube.rotateFace(Face.TOP, -90);
		cube.rotateFace(Face.RIGHT, -90);
		assertTrue(cube.isPieceSolved(topcorner));
		assertTrue(cube.isPieceSolved(topedge));
		assertTrue(cube.isPieceSolved(middleedge));
		assertTrue(cube.isPieceSolved(bottomcorner));
	}

	@Test
	final void testRubiksCube_RotateWithVisualOutput() {
		RubiksCube cube = RubiksCubeManager.getSolvedCube();
		testResults.add(new TestResult("Solved cube", cube.getDisplayData()));

		cube.rotateFace(Face.TOP, 90);
		testResults.add(new TestResult("Rotate TOP clockwise", cube.getDisplayData()));
		cube.rotateFace(Face.TOP, -90);

		cube.rotateFace(Face.BOTTOM, 90);
		testResults.add(new TestResult("Rotate BOTTOM clockwise", cube.getDisplayData()));
		cube.rotateFace(Face.BOTTOM, -90);

		cube.rotateFace(Face.LEFT, 90);
		testResults.add(new TestResult("Rotate LEFT clockwise", cube.getDisplayData()));
		cube.rotateFace(Face.LEFT, -90);

		cube.rotateFace(Face.RIGHT, 90);
		testResults.add(new TestResult("Rotate RIGHT clockwise", cube.getDisplayData()));
		cube.rotateFace(Face.RIGHT, -90);

		cube.rotateFace(Face.FRONT, 90);
		testResults.add(new TestResult("Rotate FRONT clockwise", cube.getDisplayData()));
		cube.rotateFace(Face.FRONT, -90);
		cube.rotateFace(Face.BACK, 90);

		testResults.add(new TestResult("Rotate BACK clockwise", cube.getDisplayData()));
		cube.rotateFace(Face.BACK, -90);

		cube.rotateCube(Face.TOP, 90);
		testResults.add(new TestResult("Rotate Cube - TOP", cube.getDisplayData()));

		cube.rotateCube(Face.BOTTOM, 90);
		testResults.add(new TestResult("Rotate Cube - BOTTOM", cube.getDisplayData()));

		cube.rotateCube(Face.LEFT, 90);
		testResults.add(new TestResult("Rotate Cube - LEFT", cube.getDisplayData()));

		cube.rotateCube(Face.RIGHT, 90);
		testResults.add(new TestResult("Rotate Cube - RIGHT", cube.getDisplayData()));

		cube.rotateCube(Face.FRONT, 90);
		testResults.add(new TestResult("Rotate Cube - FRONT", cube.getDisplayData()));

		cube.rotateCube(Face.BACK, 90);
		testResults.add(new TestResult("Rotate Cube - BACK", cube.getDisplayData()));
	}

	@Test
	final void testRubiksCube_RandomMoves() {
		RubiksCube cube = RubiksCubeManager.getSolvedCube();

		final Move[] ALLMOVES = { new Move(Face.FRONT, 90), new Move(Face.FRONT, -90), new Move(Face.BACK, 90),
				new Move(Face.BACK, -90), new Move(Face.LEFT, 90), new Move(Face.LEFT, -90), new Move(Face.RIGHT, 90),
				new Move(Face.RIGHT, -90), new Move(Face.TOP, 90), new Move(Face.TOP, -90), new Move(Face.BOTTOM, 90),
				new Move(Face.BOTTOM, -90) };
		Move[] moves = new Move[20];
		for (int i = 0; i < 20; i++) {
			moves[i] = ALLMOVES[(int) (Math.random() * ALLMOVES.length)];
		}

		testResults.add(new TestResult("Solved cube", cube.getDisplayData()));

		for (Move m : moves) {
			cube.rotateFace(m.getFace(), m.getAngle());
		}

		testResults.add(new TestResult("After random moves", cube.getDisplayData()));

		for (int i = moves.length - 1; i >= 0; i--) {
			cube.rotateFace(moves[i].getFace(), (moves[i].getAngle() * -1));
		}
		testResults.add(new TestResult("After reversing previous moves", cube.getDisplayData()));
	}

	@Test
	final void testRubiksCube_ToJSON() {
		RubiksCube cube = RubiksCubeManager.getSolvedCube();
		Gson gson = new Gson();
		String json = gson.toJson(cube.getDisplayData());
		TestHelper.WriteJSON("RubiksCube.json", json);
	}

	@Test
	final void testRubiksCube_getDisplayData() {
		RubiksCube cube = RubiksCubeManager.getRandomCube();
		DisplayData display = cube.getDisplayData();

		Piece bottomLeftBack = cube.getPieceByLocation(new Point3D(-1, -1, -1));
		assertEquals(bottomLeftBack.getXColor().toString(), display.left()[2][0]);
		assertEquals(bottomLeftBack.getYColor().toString(), display.bottom()[2][0]);
		assertEquals(bottomLeftBack.getZColor().toString(), display.back()[2][2]);

		Piece topRightFront = cube.getPieceByLocation(new Point3D(1, 1, 1));
		assertEquals(topRightFront.getXColor().toString(), display.right()[0][0]);
		assertEquals(topRightFront.getYColor().toString(), display.top()[2][2]);
		assertEquals(topRightFront.getZColor().toString(), display.front()[0][2]);

		Piece bottomLeftFront = cube.getPieceByLocation(new Point3D(-1, -1, 1));
		assertEquals(bottomLeftFront.getXColor().toString(), display.left()[2][2]);
		assertEquals(bottomLeftFront.getYColor().toString(), display.bottom()[0][0]);
		assertEquals(bottomLeftFront.getZColor().toString(), display.front()[2][0]);

		Piece topRightBack = cube.getPieceByLocation(new Point3D(1, 1, -1));
		assertEquals(topRightBack.getXColor().toString(), display.right()[0][2]);
		assertEquals(topRightBack.getYColor().toString(), display.top()[0][2]);
		assertEquals(topRightBack.getZColor().toString(), display.back()[0][0]);

		Piece leftFront = cube.getPieceByLocation(new Point3D(-1, 0, 1));
		assertEquals(leftFront.getXColor().toString(), display.left()[1][2]);
		assertEquals(leftFront.getZColor().toString(), display.front()[1][0]);

		Piece rightBack = cube.getPieceByLocation(new Point3D(1, 0, -1));
		assertEquals(rightBack.getXColor().toString(), display.right()[1][2]);
		assertEquals(rightBack.getZColor().toString(), display.back()[1][0]);

		Piece topFront = cube.getPieceByLocation(new Point3D(0, 1, 1));
		assertEquals(topFront.getYColor().toString(), display.top()[2][1]);
		assertEquals(topFront.getZColor().toString(), display.front()[0][1]);

		Piece bottomBack = cube.getPieceByLocation(new Point3D(0, -1, -1));
		assertEquals(bottomBack.getYColor().toString(), display.bottom()[2][1]);
		assertEquals(bottomBack.getZColor().toString(), display.back()[2][1]);
	}

	@Test
	final void testRubiksCube_FromDisplayData() {
		RubiksCube cube1 = RubiksCubeManager.getSolvedCube();
		DisplayData initialData = cube1.getDisplayData();
		RubiksCube cube2 = new RubiksCube(initialData);
		DisplayData newData = cube2.getDisplayData();

		assertArrayEquals(initialData.top(), newData.top());
		assertArrayEquals(initialData.bottom(), newData.bottom());
		assertArrayEquals(initialData.left(), newData.left());
		assertArrayEquals(initialData.right(), newData.right());
		assertArrayEquals(initialData.front(), newData.front());
		assertArrayEquals(initialData.back(), newData.back());

		cube1 = RubiksCubeManager.getRandomCube();
		initialData = cube1.getDisplayData();
		cube2 = new RubiksCube(initialData);
		newData = cube2.getDisplayData();

		assertArrayEquals(initialData.top(), newData.top());
		assertArrayEquals(initialData.bottom(), newData.bottom());
		assertArrayEquals(initialData.left(), newData.left());
		assertArrayEquals(initialData.right(), newData.right());
		assertArrayEquals(initialData.front(), newData.front());
		assertArrayEquals(initialData.back(), newData.back());
	}

	@Test
	final void testRubiksCube_getStateIdentifier() {
		RubiksCube solvedCube = RubiksCubeManager.getSolvedCube();
		assertEquals("0-5zzzz-17777-3llll-2eeee-4ssss", solvedCube.getStateIdentifier());

		RubiksCube cube = RubiksCubeManager.getRandomCube();
		String initialID = cube.getStateIdentifier();

		cube.rotateFace(Face.FRONT, 90);
		assertNotEquals(initialID, cube.getStateIdentifier());

		cube.rotateFace(Face.FRONT, -90);
		assertEquals(initialID, cube.getStateIdentifier());
	}

	@Test
	final void testRubiksCube_FromID() {
		RubiksCube solved = new RubiksCube("0-5zzzz-17777-3llll-2eeee-4ssss");
		assertTrue(solved.isCubeSolved());

		RubiksCube cube1 = RubiksCubeManager.getRandomCube();
		String id1 = cube1.getStateIdentifier();
		RubiksCube cube2 = new RubiksCube(id1);
		assertEquals(id1, cube2.getStateIdentifier());

		try {
			cube1 = new RubiksCube("12345");
			fail("Expected InvalidRubiksCubeState");
		} catch (InvalidRubiksCubeState e) {
			assertEquals("Invalid state identifier: 12345", e.getMessage());
		}

	}
}
