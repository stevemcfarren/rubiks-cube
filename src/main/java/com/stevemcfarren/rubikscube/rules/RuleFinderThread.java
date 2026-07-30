package com.stevemcfarren.rubikscube.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.stevemcfarren.rubikscube.Color;
import com.stevemcfarren.rubikscube.Move;
import com.stevemcfarren.rubikscube.Piece;
import com.stevemcfarren.rubikscube.Point3D;
import com.stevemcfarren.rubikscube.RubiksCube;
import com.stevemcfarren.rubikscube.RubiksCube.Face;
import com.stevemcfarren.rubikscube.RubiksCubeManager;

public class RuleFinderThread implements Runnable {
	/** The cube being manipulated during the search */
	protected final RubiksCube cube;
	/** Latch to signal when the search is complete */
	private final CountDownLatch latch;

	/** Whether a solving rule was found */
	protected boolean status;
	/** The list of moves that solve the cube */
	protected List<Move> moves;

	/** The number of solving rules found */
	protected int ruleCount = 0;

	private final static Point3D[] TOPEDGES = { new Point3D(-1, 1, 0), new Point3D(0, 1, -1), new Point3D(1, 1, 0) };
	private final static Point3D[] TOPCORNERS = { new Point3D(-1, 1, -1), new Point3D(-1, 1, 1),
			new Point3D(1, 1, -1) };
	private final static Point3D[] MIDDLEEDGES = { new Point3D(-1, 0, -1), new Point3D(-1, 0, 1),
			new Point3D(1, 0, -1) };
	private final static Point3D[] BOTTOMCORNERS = { new Point3D(-1, -1, -1), new Point3D(-1, -1, 1),
			new Point3D(1, -1, -1), new Point3D(1, -1, 1) };
	private final static Point3D[] BOTTOMEDGES = { new Point3D(-1, -1, 0), new Point3D(0, -1, -1), new Point3D(0, -1, 1), new Point3D(1, -1, 0) };

	/**
	 * Maximum sequence length to search. Note that the number of sequences and
	 * corresponding run time grows exponentially with length.
	 */
	private final int maxLength;
	/**
	 * Minimum sequence length to search.
	 */
	private final int minLength;

	/**
	 * Constructs a RuleFinderThread
	 *
	 * @param initialMoves An initial set of moves to perform before searching for
	 *                     rules. This allows an orchestrator to divide the search
	 *                     among multiple thread.
	 * @param latch        the latch to countdown when search is complete (may be
	 *                     null)
	 */
	public RuleFinderThread(List<Move> initialMoves, int minLength, int maxLength, CountDownLatch latch) {
		this.cube = RubiksCubeManager.getSolvedCube();
		this.latch = latch;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.status = false;
		this.moves = new ArrayList<Move>();

		if (initialMoves != null) {
			for (Move m : initialMoves) {
				moves.add(m);
				cube.rotateFace(m.getFace(), m.getAngle());
			}
		}
	}

	private PieceState getPieceState(Point3D location) {
		Piece p = cube.getPieceByLocation(location);
		return new PieceState(p, location);
	}

	// TODO
	//private PieceState getPieceIDState(Point3D location) {
	//	Piece p = cube.getPieceByLocation(location);
	//	return new PieceState(p.getID(), location);
	//}

	/**
	 * Gets whether a solving rule was found.
	 *
	 * @return true if a rule was found, false otherwise
	 */
	public boolean getStatus() {
		return this.status;
	}

	/**
	 * Gets the list of moves that solve the cube.
	 *
	 * @return the list of moves, empty if no solution found
	 */
	public List<Move> getMoves() {
		return moves;
	}

	/**
	 * Checks if the cube is in one of the known target states and if so logs the
	 * rule.
	 *
	 * @return true if a rule was found, false otherwise
	 */
	public boolean checkTargetStates() {
		// Top Edges
		Point3D currentLocation = cube.findPieceByColor(Color.NONE, RubiksCubeManager.getNormalizedColor(Face.TOP),
				RubiksCubeManager.getNormalizedColor(Face.FRONT));
		for (Point3D l : TOPEDGES) {
			if (!cube.isPieceSolved(l) && !currentLocation.equals(l))
				return false;
		}
		if (!cube.isPieceSolved(new Point3D(0, 1, 1))) {
			RuleManager.addTopEdgeRule(getPieceState(currentLocation),
					RuleHelper.getReverseSequence(moves));
			return true;
		}

		// Top Corners
		currentLocation = cube.findPieceByColor(RubiksCubeManager.getNormalizedColor(Face.RIGHT),
				RubiksCubeManager.getNormalizedColor(Face.TOP), RubiksCubeManager.getNormalizedColor(Face.FRONT));
		for (Point3D l : TOPCORNERS) {
			if (!cube.isPieceSolved(l) && !currentLocation.equals(l))
				return false;
		}
		if (!cube.isPieceSolved(new Point3D(1, 1, 1))) {
			RuleManager.addTopCornerRule(getPieceState(currentLocation),
					RuleHelper.getReverseSequence(moves));
			return true;
		}

		// Middle Edges
		currentLocation = cube.findPieceByColor(RubiksCubeManager.getNormalizedColor(Face.RIGHT), Color.NONE,
				RubiksCubeManager.getNormalizedColor(Face.FRONT));
		for (Point3D l : MIDDLEEDGES) {
			if (!cube.isPieceSolved(l) && !currentLocation.equals(l))
				return false;
		}
		if (!cube.isPieceSolved(new Point3D(1, 0, 1))) {
			RuleManager.addMiddleEdgeRule(getPieceState(currentLocation),
					RuleHelper.getReverseSequence(moves));
			return true;
		}


		// Bottom Corners
		// First make sure the back right corner is in place (define corner swap rules as
		// swapping only other three corners)
		if (!cube.isPieceInPlace(new Point3D(1, -1, -1))) {
			return false;
		}
		int cornersSolved = 0;
		int cornersInPlace = 0;
		for (Point3D l : BOTTOMCORNERS) {
			if (cube.isPieceSolved(l)) {
				cornersSolved++;
				cornersInPlace++;
			} else if (cube.isPieceInPlace(l)) {
				cornersInPlace++;
			}
		}
		
		if (cornersInPlace < 4) {
			// Bottom corners out of place means we found a corner swap rule.
			PieceState[] currentState = new PieceState[4];
			int count = 0;
			for (Point3D l : BOTTOMCORNERS) {
				currentState[count++] = getPieceState(l);
			}

			RuleManager.addCornerSwapRule(currentState, RuleHelper.getReverseSequence(moves));
			return true;
		} else if (cornersSolved < 4) {
			// All four corners in place but not solved means we found a corner rotate rule.
			PieceState[] currentState = new PieceState[4];
			int count = 0;
			for (Point3D l : BOTTOMCORNERS) {
				currentState[count++] = getPieceState(l);
			}

			RuleManager.addCornerRotateRule(currentState, RuleHelper.getReverseSequence(moves));
			return true;
		}

		
		
		// Bottom Edges
		int edgesSolved = 0;
		for (Point3D l : BOTTOMEDGES) {
			if (cube.isPieceSolved(l)) {
				edgesSolved++;
			}
		}
		
		if (edgesSolved < 4) {
			// Pieces out of place means we found a swap rule.
			PieceState[] currentState = new PieceState[4];
			int count = 0;
			for (Point3D l : BOTTOMEDGES) {
				currentState[count++] = getPieceState(l);
			}

			RuleManager.addEdgeSwapRule(currentState, RuleHelper.getReverseSequence(moves));
			return true;
		}
		
		
		return false;
	}


	/**
	 * Test all sequences of length 1 through MAX_SEQUENCE until a rule is found to
	 * match the specified target state.
	 */
	protected void findRules() {
		int start = Math.max(minLength,  moves.size()+1);
		for (int i = start; i <= maxLength; i++) {
			//System.out.println("Testing sequences of length " + i);
			testAllSequences(i);
		}
	}

	/**
	 * Test all sequences of the given length and record all sequences that reach
	 * the target.
	 */
	protected void testAllSequences(int sequenceLength) {
		if (checkTargetStates()) {
			ruleCount++;
		}

		if (sequenceLength <= moves.size()) {
			return;
		}

		for (Move m : RuleHelper.ALLMOVES) {
			if (RuleHelper.isMoveWasted(moves, m)) {
				continue;
			}

			moves.add(m);
			cube.rotateFace(m.getFace(), m.getAngle());

			testAllSequences(sequenceLength);

			moves.remove(moves.size() - 1);
			cube.rotateFace(m.getFace(), (-1 * m.getAngle()));
		}
	}

	@Override
	public void run() {

		findRules();

		// System.out.println("Number of rules found: " + ruleCount);

		this.status = (ruleCount > 0);

		if (latch != null) {
			latch.countDown();
		}
	}
}
