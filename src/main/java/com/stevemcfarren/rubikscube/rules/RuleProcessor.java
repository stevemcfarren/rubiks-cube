package com.stevemcfarren.rubikscube.rules;

import java.io.FileReader;

import com.google.gson.Gson;
import com.stevemcfarren.rubikscube.Move;
import com.stevemcfarren.rubikscube.Point3D;
import com.stevemcfarren.rubikscube.RubiksCube;
import com.stevemcfarren.rubikscube.RubiksCubeManager;

public class RuleProcessor {
	private RuleSet ruleFinderRules = null;

	private final static Point3D[] BOTTOMCORNERS = { new Point3D(-1, -1, -1), new Point3D(-1, -1, 1),
			new Point3D(1, -1, -1), new Point3D(1, -1, 1) };
	private final static Point3D[] BOTTOMEDGES = { new Point3D(-1, -1, 0), new Point3D(1, -1, 0),
			new Point3D(0, -1, -1), new Point3D(0, -1, 1) };

	public RuleProcessor() {
		Gson gson = new Gson();

		try {
			FileReader reader = new FileReader("RubiksCube_RuleFinder_RuleSet_12.json");
			ruleFinderRules = gson.fromJson(reader, RuleSet.class);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processTopEdgeRules() {
		// No processing needed, just copy rules to final rule set.
		for (Rule r : ruleFinderRules.topEdgeRules) {
			RuleManager.addTopEdgeRule(r.getInitialState()[0], r.getMoves());
		}
	}

	private void processTopCornerRules() {
		// No processing needed, just copy rules to final rule set.
		for (Rule r : ruleFinderRules.topCornerRules) {
			RuleManager.addTopCornerRule(r.getInitialState()[0], r.getMoves());
		}
	}

	private void processMiddleEdgeRules() {
		// No processing needed, just copy rules to final rule set.
		for (Rule r : ruleFinderRules.middleEdgeRules) {
			RuleManager.addMiddleEdgeRule(r.getInitialState()[0], r.getMoves());
		}
	}

	private void processCornerSwapRules() {
		// Rule finder added full state including piece orientation. This is not needed
		// so rules will be rebuilt with only piece ID, reducing the rule set to 5
		// distinct rules.
		for (Rule r : ruleFinderRules.cornerSwapRules) {
			PieceState[] newState = new PieceState[4];
			for (int i = 0; i < 4; i++) {
				PieceState oldState = r.getInitialState()[i];
				newState[i] = new PieceState(oldState.pieceID, oldState.location);
			}
			RuleManager.addCornerSwapRule(newState, r.getMoves());
		}
	}

	private void processCornerRotates() {
		// The rule finder only found 22 bottom corner rotate rules, but there should be
		// 26. The 4 missing scenarios can be solved by combining two rotate rules.
		//
		// Theoretically, there are 81 (3^4) possible corner orientations. However, You
		// cannot twist a single corner piece by itself. The sum of all corner twists
		// must always balance out. This eliminates 2/3 of all corner orientation
		// possibilities.

		// First copy the found rules.
		for (Rule r : ruleFinderRules.cornerRotateRules) {
			RuleManager.addCornerRotateRule(r.getInitialState(), r.getMoves());
		}

		// Next find pairs of rules that can solve the scenarios for which a rule was
		// not found.
		for (Rule r1 : ruleFinderRules.cornerRotateRules) {
			for (Rule r2 : ruleFinderRules.cornerRotateRules) {

				// For each pair of rules, start at the end state (solve cube) and apply the
				// rules in reverse to determine the initial state that the rule pair will
				// solve.
				RubiksCube cube = RubiksCubeManager.getSolvedCube();

				for (int i = r2.getMoves().length; i > 0; i--) {
					Move m = r2.getMoves()[i - 1];
					cube.rotateFace(m.getFace(), RuleHelper.reverseDirection(m.getDirection()));
				}

				for (int i = r1.getMoves().length; i > 0; i--) {
					Move m = r1.getMoves()[i - 1];
					cube.rotateFace(m.getFace(), RuleHelper.reverseDirection(m.getDirection()));
				}

				PieceState[] currentState = new PieceState[4];
				int count = 0;
				int cornersSolved = 0;

				for (Point3D l : BOTTOMCORNERS) {
					cornersSolved += (cube.isPieceSolved(l)) ? 1 : 0;
					currentState[count++] = new PieceState(cube.getPieceByLocation(l), l);
				}

				// If the rule combination solves 1 or more pieces, add it as a new rule (the
				// RuleManager will ignore this if it is a duplicate).
				if (cornersSolved < 4) {
					Move[] combinedMoves = new Move[r1.getMoves().length + r2.getMoves().length];
					System.arraycopy(r1.getMoves(), 0, combinedMoves, 0, r1.getMoves().length);
					System.arraycopy(r2.getMoves(), 0, combinedMoves, r1.getMoves().length, r2.getMoves().length);

					RuleManager.addCornerRotateRule(currentState, combinedMoves);
				}
			}
		}

	}

	private void processBottomEdgeRules() {
		// The rule finder only found 41 bottom edge swap rules, but there should be
		// 95. The missing scenarios can be solved by combining two edge swap rules.
		//
		// Theoretically, there are 384 (4! * 2^4) possible bottom edge scenarios.
		// However, You cannot flip a single edge piece in place and you cannot swap
		// just two edges. Due to these restrictions, only 1/4 of the 384 scenarios are
		// possible. One of these scenarios is the solved state, leaving 95 rules
		// needed.

		// First copy the found rules.
		for (Rule r : ruleFinderRules.edgeSwapRules) {
			RuleManager.addEdgeSwapRule(r.getInitialState(), r.getMoves());
		}

		// Next find pairs of rules that can solve the scenarios for which a rule was
		// not found.
		for (Rule r1 : ruleFinderRules.edgeSwapRules) {
			for (Rule r2 : ruleFinderRules.edgeSwapRules) {

				// For each pair of rules, start at the end state (solve cube) and apply the
				// rules in reverse to determine the initial state that the rule pair will
				// solve.
				RubiksCube cube = RubiksCubeManager.getSolvedCube();

				for (int i = r2.getMoves().length; i > 0; i--) {
					Move m = r2.getMoves()[i - 1];
					cube.rotateFace(m.getFace(), RuleHelper.reverseDirection(m.getDirection()));
				}

				for (int i = r1.getMoves().length; i > 0; i--) {
					Move m = r1.getMoves()[i - 1];
					cube.rotateFace(m.getFace(), RuleHelper.reverseDirection(m.getDirection()));
				}

				PieceState[] currentState = new PieceState[4];
				int count = 0;
				int edgesSolved = 0;

				for (Point3D l : BOTTOMEDGES) {
					edgesSolved += (cube.isPieceSolved(l)) ? 1 : 0;
					currentState[count++] = new PieceState(cube.getPieceByLocation(l), l);
				}

				// If the rule combination solves 1 or more pieces, add it as a new rule (the
				// RuleManager will ignore this if it is a duplicate).
				if (edgesSolved < 4) {
					Move[] combinedMoves = new Move[r1.getMoves().length + r2.getMoves().length];
					System.arraycopy(r1.getMoves(), 0, combinedMoves, 0, r1.getMoves().length);
					System.arraycopy(r2.getMoves(), 0, combinedMoves, r1.getMoves().length, r2.getMoves().length);

					RuleManager.addEdgeSwapRule(currentState, combinedMoves);
				}
			}
		}
	}

	public static void main(String[] args) {
		RuleProcessor processor = new RuleProcessor();

		processor.processTopEdgeRules();
		processor.processTopCornerRules();
		processor.processMiddleEdgeRules();
		processor.processCornerSwapRules();
		processor.processCornerRotates();
		processor.processBottomEdgeRules();

		RuleManager.printRuleCounts();
	}

}
