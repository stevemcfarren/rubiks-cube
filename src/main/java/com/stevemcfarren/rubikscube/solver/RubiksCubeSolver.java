package com.stevemcfarren.rubikscube.solver;

import java.util.ArrayList;
import java.util.List;

import com.stevemcfarren.rubikscube.Color;
import com.stevemcfarren.rubikscube.Move;
import com.stevemcfarren.rubikscube.Piece;
import com.stevemcfarren.rubikscube.Point3D;
import com.stevemcfarren.rubikscube.RubiksCube;
import com.stevemcfarren.rubikscube.RubiksCube.Face;
import com.stevemcfarren.rubikscube.rules.PieceState;
import com.stevemcfarren.rubikscube.rules.Rule;
import com.stevemcfarren.rubikscube.rules.RuleManager;

/**
 * Solves a Rubik's Cube using a layer-by-layer approach. The solver works by
 * solving pieces in a specific order: top edges, top corners, middle edges, and
 * finally bottom corners. It uses a rule-based approach where solving sequences
 * are learned and reused.
 */
public class RubiksCubeSolver {
	private final RubiksCube cube;
	private List<Step> steps;

	/**
	 * Represents the types of steps in solving the Rubik's Cube.
	 */
	public enum StepType {
		TOP_EDGE, TOP_CORNER, MIDDLE_EDGE, BOTTOM_CORNER_POSITION, BOTTOM_CORNER_ORIENTATION, BOTTOM_EDGES
	};

	/**
	 * Face by face visual representation of the cube.
	 */
	public record Step(StepType type, List<Move> moves) {
	}

	/**
	 * Constructs a new RubiksCubeSolver for the given cube.
	 *
	 * @param cube the cube to solve
	 */
	public RubiksCubeSolver(RubiksCube cube) {
		this.cube = cube;
		steps = new ArrayList<Step>();
	}

	/**
	 * Gets the number of moves made during the solving process.
	 *
	 * @return the total move count
	 */
	public long getMoveCount() {
		int totalMoves = 0;
		for (Step s : steps) {
			totalMoves += s.moves().size();
		}
		return totalMoves;
	}

	/**
	 * Gets the steps used to solve the cube.
	 *
	 * @return list of steps.
	 */
	public List<Step> getSteps() {
		return steps;
	}

	private void beginStep(StepType type) {
		steps.add(new Step(type, new ArrayList<Move>()));
	}
	
	private void rotateCube(Face face, int angle) {
		if (!steps.isEmpty()) {
			List<Move> moves = steps.getLast().moves();
			moves.add(new Move(Move.Type.CUBE, face, angle));
		}
		cube.rotateCube(face, angle);
	}

	private void rotateFace(Face face, int angle) {
		if (!steps.isEmpty()) {
			List<Move> moves = steps.getLast().moves();
			moves.add(new Move(Move.Type.FACE, face, angle));
		}
		cube.rotateFace(face, angle);
	}

	/**
	 * Attempts to solve the entire cube using a layer-by-layer approach. The solver
	 * first solves the top edge pieces, then top corners, then middle edges, and
	 * finally bottom corners.
	 *
	 * @return true if the cube was successfully solved, false otherwise
	 */
	public boolean solveCube() {
		steps.clear();

		if (!solveTopEdgePieces()) {
			return false;
		}

		if (!solveTopCornerPieces()) {
			return false;
		}

		if (!solveMiddleEdgePieces()) {
			return false;
		}

		if (!solveBottomCorners()) {
			return false;
		}

		if (!solveBottomEdges()) {
			return false;
		}

		return true;
	}

	protected boolean solveTopEdgePieces() {
		// First solve edge pieces.
		System.out.println("Solving top edge pieces...");
		int piecesSolved = 0;

		for (int i = 0; i < 4; i++) {
			beginStep(StepType.TOP_EDGE);
			rotateCube(Face.TOP, 90);
			if (this.solveTopFrontEdge()) {
				piecesSolved++;
			}
		}

		System.out.println("    Top edge pieces solved: " + piecesSolved);

		if (piecesSolved < 4)
			return false;

		return true;
	}

	protected boolean solveTopCornerPieces() {
		// Solve top right corner pieces.
		System.out.println("Solving top corner pieces...");
		int piecesSolved = 0;

		for (int i = 0; i < 4; i++) {
			beginStep(StepType.TOP_CORNER);
			rotateCube(Face.TOP, 90);
			if (this.solveTopFrontRightCorner()) {
				piecesSolved++;
			}
		}

		System.out.println("    Top corner pieces solved: " + piecesSolved);

		if (piecesSolved < 4)
			return false;

		return true;
	}

	protected boolean solveMiddleEdgePieces() {
		// Solve middle edge pieces.
		System.out.println("Solving middle edge pieces...");
		int piecesSolved = 0;

		for (int i = 0; i < 4; i++) {
			beginStep(StepType.MIDDLE_EDGE);
			rotateCube(Face.TOP, 90);
			if (this.solveMiddleEdge()) {
				piecesSolved++;
			}
		}

		System.out.println("    Middle edge pieces solved: " + piecesSolved);

		if (piecesSolved < 4)
			return false;

		return true;
	}

	protected boolean solveBottomCorners() {
		// Solve bottom corner pieces.
		System.out.println("Solving bottom corner pieces...");
		beginStep(StepType.BOTTOM_CORNER_POSITION);

		Point3D frontLeft = new Point3D(-1, -1, 1);
		Point3D frontRight = new Point3D(1, -1, 1);
		Point3D backLeft = new Point3D(-1, -1, -1);
		Point3D backRight = new Point3D(1, -1, -1);

		// Rotate the bottom until the back right piece is in place.
		int backRightSolvedID = cube.getColorByFace(Face.RIGHT).bitmask + cube.getColorByFace(Face.BOTTOM).bitmask
				+ cube.getColorByFace(Face.BACK).bitmask;
		Piece backRightPiece = cube.getPieceByLocation(backRight);
		while (backRightPiece.getID() != backRightSolvedID) {
			rotateFace(Face.BOTTOM, 90);
			backRightPiece = cube.getPieceByLocation(backRight);
		}

		// If any of the remaining corners are not in place, use corner swap rules to put them in place.
		if (!cube.isPieceInPlace(frontLeft) || !cube.isPieceInPlace(frontRight) || !cube.isPieceInPlace(backLeft)) {
			PieceState[] normalizedInitialState = new PieceState[4];
			normalizedInitialState[0] = new PieceState(RuleManager.getNormalizedPieceID(cube, frontLeft), frontLeft);
			normalizedInitialState[1] = new PieceState(RuleManager.getNormalizedPieceID(cube, frontRight), frontRight);
			normalizedInitialState[2] = new PieceState(RuleManager.getNormalizedPieceID(cube, backLeft), backLeft);
			normalizedInitialState[3] = new PieceState(RuleManager.getNormalizedPieceID(cube, backRight), backRight);

			Rule rule = RuleManager.findCornerSwapRule(normalizedInitialState);
			if (rule != null) {
				// apply rule
				for (Move m : rule.getMoves()) {
					rotateFace(m.getFace(), m.getAngle());
				}
			} else {
				// Didn't find rule to put corners in place.
				System.out.println("Failed to find a rule to put the bottom corners in position.");
				return false;
			}
		}

		System.out.println("    Bottom corner pieces are in place");
		beginStep(StepType.BOTTOM_CORNER_ORIENTATION);

		// If any bottom corners are not solved, find a rule to rotate them in place.
		if (!cube.isPieceSolved(frontLeft) || !cube.isPieceSolved(frontRight) || !cube.isPieceSolved(backLeft) || !cube.isPieceSolved(backRight)) {
			PieceState[] normalizedInitialState = new PieceState[4];
			normalizedInitialState[0] = new PieceState(RuleManager.getNormalizedPiece(cube, cube.getPieceByLocation(frontLeft)), frontLeft);
			normalizedInitialState[1] = new PieceState(RuleManager.getNormalizedPiece(cube, cube.getPieceByLocation(frontRight)), frontRight);
			normalizedInitialState[2] = new PieceState(RuleManager.getNormalizedPiece(cube, cube.getPieceByLocation(backLeft)), backLeft);
			normalizedInitialState[3] = new PieceState(RuleManager.getNormalizedPiece(cube, cube.getPieceByLocation(backRight)), backRight);
			
			Rule rule = RuleManager.findCornerRotateRule(normalizedInitialState);
			if (rule != null) {
				// apply rule
				for (Move m : rule.getMoves()) {
					rotateFace(m.getFace(), m.getAngle());
				}
			} else {
				// Didn't find rule to put corners in place.
				System.out.println("Failed to find a rule to rotate corners in place.");
				return false;
			}
		}

		System.out.println("    Bottom corner pieces are in solved");

		return true;
	}

	protected boolean solveBottomEdges() {
		// Solve bottom corner pieces.
		System.out.println("Solving bottom corner pieces...");
		beginStep(StepType.BOTTOM_EDGES);

		Point3D left = new Point3D(-1, -1, 0);
		Point3D right = new Point3D(1, -1, 0);
		Point3D front = new Point3D(0, -1, 1);
		Point3D back = new Point3D(0, -1, -1);

		// If any bottom edges are not solved, find a rule to solve them.
		if (!cube.isPieceSolved(left) || !cube.isPieceSolved(right) || !cube.isPieceSolved(front) || !cube.isPieceSolved(back)) {
			PieceState[] normalizedInitialState = new PieceState[4];
			normalizedInitialState[0] = new PieceState(RuleManager.getNormalizedPiece(cube, cube.getPieceByLocation(left)), left);
			normalizedInitialState[1] = new PieceState(RuleManager.getNormalizedPiece(cube, cube.getPieceByLocation(right)), right);
			normalizedInitialState[2] = new PieceState(RuleManager.getNormalizedPiece(cube, cube.getPieceByLocation(front)), front);
			normalizedInitialState[3] = new PieceState(RuleManager.getNormalizedPiece(cube, cube.getPieceByLocation(back)), back);
			
			Rule rule = RuleManager.findEdgeSwapRule(normalizedInitialState);
			if (rule != null) {
				// apply rule
				for (Move m : rule.getMoves()) {
					rotateFace(m.getFace(), m.getAngle());
				}
			} else {
				// Didn't find rule to solve bottom edges.
				System.out.println("Failed to find a rule to solve bottom edges.");
				return false;
			}
		}

		System.out.println("    Bottom edge pieces are solved");


		return true;
	}
	
	protected boolean solveTopFrontRightCorner() {

		// find piece with two colors matching top and front centers
		Color top = cube.getColorByFace(Face.TOP);
		Color front = cube.getColorByFace(Face.FRONT);
		Color right = cube.getColorByFace(Face.RIGHT);
		Point3D curLoc = cube.findPieceByColor(right, top, front);
		Piece p = cube.getPieceByLocation(curLoc);

		// If piece is in proper location & orientation then nothing to do
		if (curLoc.equals(new Point3D(1, 1, 1)) && p.getXColor() == right && p.getYColor() == top
				&& p.getZColor() == front) {
			return true;
		}

		Piece normalizedPiece = RuleManager.getNormalizedPiece(cube, p);
		Rule rule = RuleManager.findTopCornerRule(new PieceState(normalizedPiece, curLoc));
		if (rule != null) {
			// apply rule and return
			for (Move m : rule.getMoves()) {
				rotateFace(m.getFace(), m.getAngle());
			}
			return true;
		}

		return false;
	}

	/**
	 * Solve the top front edge position of the cube (in it's current orientation).
	 * 
	 * @param ruleFinderLatch A latch to signal when the rule finder thread
	 *                        complete. if null no rule finder thread will be
	 *                        started.
	 * 
	 * @return True if the position is solved, false if the position is not solved.
	 *         If not solved and a latch was provided, a rule finder thread will be
	 *         started.
	 */
	protected boolean solveTopFrontEdge() {

		// find piece with two colors matching top and front centers
		Color top = cube.getColorByFace(Face.TOP);
		Color front = cube.getColorByFace(Face.FRONT);
		Point3D curLoc = cube.findPieceByColor(Color.NONE, top, front);
		Piece p = cube.getPieceByLocation(curLoc);

		// If piece is in proper location & orientation then nothing to do
		if (curLoc.equals(new Point3D(0, 1, 1)) && p.getYColor() == top && p.getZColor() == front) {
			return true;
		}

		Piece normalizedPiece = RuleManager.getNormalizedPiece(cube, p);
		Rule rule = RuleManager.findTopEdgeRule(new PieceState(normalizedPiece, curLoc));
		if (rule != null) {
			// apply rule and return
			for (Move m : rule.getMoves()) {
				rotateFace(m.getFace(), m.getAngle());
			}
			return true;
		}

		return false;
	}

	protected boolean solveMiddleEdge() {

		// find piece with two colors matching top and front centers
		Color front = cube.getColorByFace(Face.FRONT);
		Color right = cube.getColorByFace(Face.RIGHT);
		Point3D curLoc = cube.findPieceByColor(right, Color.NONE, front);
		Piece p = cube.getPieceByLocation(curLoc);

		// If piece is in proper location & orientation then nothing to do
		if (curLoc.equals(new Point3D(1, 0, 1)) && p.getXColor() == right && p.getYColor() == Color.NONE
				&& p.getZColor() == front) {
			return true;
		}

		Piece normalizedPiece = RuleManager.getNormalizedPiece(cube, p);
		Rule rule = RuleManager.findMiddleEdgeRule(new PieceState(normalizedPiece, curLoc));
		if (rule != null) {
			// apply rule and return
			for (Move m : rule.getMoves()) {
				rotateFace(m.getFace(), m.getAngle());
			}

			return true;
		}

		return false;
	}

}
