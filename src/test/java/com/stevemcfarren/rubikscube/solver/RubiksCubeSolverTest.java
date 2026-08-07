package com.stevemcfarren.rubikscube.solver;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import com.google.gson.Gson;
import com.stevemcfarren.rubikscube.Move.Direction;
import com.stevemcfarren.rubikscube.Point3D;
import com.stevemcfarren.rubikscube.RubiksCube;
import com.stevemcfarren.rubikscube.RubiksCube.Face;
import com.stevemcfarren.rubikscube.RubiksCubeManager;
import com.stevemcfarren.rubikscube.TestHelper;
import com.stevemcfarren.rubikscube.TestResult;

class RubiksCubeSolverTest {
	static List<TestResult> testResults;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		testResults = new ArrayList<TestResult>();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		Gson gson = new Gson();
		String json = gson.toJson(testResults);
		TestHelper.WriteJSON("RubiksCubeSolverTestOutput.json", json);
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	
	@Test
	final void testSolveSetCube() {
		RubiksCube cube = RubiksCubeManager.getSolvedCube();
		RubiksCubeSolver solver = new RubiksCubeSolver(cube);
	
		cube.rotateFace(Face.FRONT, Direction.CLOCKWISE);
		cube.rotateFace(Face.RIGHT, Direction.COUNTERCLOCKWISE);
		cube.rotateFace(Face.BACK, Direction.CLOCKWISE);
		cube.rotateFace(Face.LEFT, Direction.COUNTERCLOCKWISE);
		cube.rotateFace(Face.TOP, Direction.CLOCKWISE);
		cube.rotateFace(Face.BOTTOM, Direction.COUNTERCLOCKWISE);
		cube.rotateFace(Face.FRONT, Direction.CLOCKWISE);
		cube.rotateFace(Face.RIGHT, Direction.COUNTERCLOCKWISE);
		cube.rotateFace(Face.BACK, Direction.CLOCKWISE);
		cube.rotateFace(Face.LEFT, Direction.COUNTERCLOCKWISE);
	
		testResults.add(new TestResult("Solve Predefined Cube - Initial State", cube.getDisplayData()));

		boolean ret = solver.solveCube();
		testResults.add(new TestResult("Solve Predefined Cube - Final State", cube.getDisplayData()));
		assertTrue(ret);

		// Top Edges
		TestHelper.assertPieceSolved(cube, new Point3D(0, 1, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(-1, 1, 0));
		TestHelper.assertPieceSolved(cube, new Point3D(1, 1, 0));
		TestHelper.assertPieceSolved(cube, new Point3D(0, 1, 1));
		// Top Corners
		TestHelper.assertPieceSolved(cube, new Point3D(-1, 1, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(-1, 1, 1));
		TestHelper.assertPieceSolved(cube, new Point3D(1, 1, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(1, 1, 1));
		// Middle Edges
		TestHelper.assertPieceSolved(cube, new Point3D(-1, 0, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(-1, 0, 1));
		TestHelper.assertPieceSolved(cube, new Point3D(1, 0, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(1, 0, 1));
		// Bottom Corners
		TestHelper.assertPieceSolved(cube, new Point3D(-1, -1, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(1, -1, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(-1, -1, 1));
		TestHelper.assertPieceSolved(cube, new Point3D(1, -1, 1));
		// Bottom Edges
		TestHelper.assertPieceSolved(cube, new Point3D(-1, -1, 0));
		TestHelper.assertPieceSolved(cube, new Point3D(1, -1, 0));
		TestHelper.assertPieceSolved(cube, new Point3D(0, -1, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(0, -1, 1));
	}
	
	@Test
	final void testSolveRandomCubes() {
		int failureCount = 0;
		for (int i=0; i<10000; i++) {
			try {
				solveRandomCube();
			}
			catch (AssertionFailedError e) {
				failureCount++;
			}
		}
		assertEquals(0, failureCount);
	}

	final void solveRandomCube() {
		RubiksCube cube = RubiksCubeManager.getRandomCube();
		RubiksCubeSolver solver = new RubiksCubeSolver(cube);
		
		testResults.add(new TestResult("Solve Random Cube - Initial State", cube.getDisplayData()));

		boolean ret = solver.solveCube();
		testResults.add(new TestResult("Solve Random Cube - Final State", cube.getDisplayData()));
		assertTrue(ret);

		// Top Edges
		TestHelper.assertPieceSolved(cube, new Point3D(0, 1, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(-1, 1, 0));
		TestHelper.assertPieceSolved(cube, new Point3D(1, 1, 0));
		TestHelper.assertPieceSolved(cube, new Point3D(0, 1, 1));
		// Top Corners
		TestHelper.assertPieceSolved(cube, new Point3D(-1, 1, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(-1, 1, 1));
		TestHelper.assertPieceSolved(cube, new Point3D(1, 1, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(1, 1, 1));
		// Middle Edges
		TestHelper.assertPieceSolved(cube, new Point3D(-1, 0, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(-1, 0, 1));
		TestHelper.assertPieceSolved(cube, new Point3D(1, 0, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(1, 0, 1));
		// Bottom Corners
		TestHelper.assertPieceSolved(cube, new Point3D(-1, -1, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(1, -1, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(-1, -1, 1));
		TestHelper.assertPieceSolved(cube, new Point3D(1, -1, 1));
		// Bottom Edges
		TestHelper.assertPieceSolved(cube, new Point3D(-1, -1, 0));
		TestHelper.assertPieceSolved(cube, new Point3D(1, -1, 0));
		TestHelper.assertPieceSolved(cube, new Point3D(0, -1, -1));
		TestHelper.assertPieceSolved(cube, new Point3D(0, -1, 1));
	}
	
}