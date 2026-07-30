package com.stevemcfarren.rubikscube.rules;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stevemcfarren.rubikscube.Color;
import com.stevemcfarren.rubikscube.Move;
import com.stevemcfarren.rubikscube.Piece;
import com.stevemcfarren.rubikscube.Point3D;
import com.stevemcfarren.rubikscube.RubiksCube;
import com.stevemcfarren.rubikscube.RubiksCubeManager;

public class RuleManager {

	private static RuleSet rules = null;
	private static String rulesFileName = "RubiksCube_RuleSet.json";

	/**
	 * Private constructor to prevent instantiation.
	 */
	private RuleManager() {
	}

	public static synchronized boolean loadRules() {
		return loadRules(null, false);
	}

	/**
	 * Loads rules from the JSON file if not already loaded.
	 *
	 * @param filename    Source rule set JSON file to read. If null or empty the
	 *                    internal rule set resource is used.
	 * @param forceReload If true rule set will be reloaded from file even if rules
	 *                    were already loaded.
	 * 
	 * @return true if rules loaded successfully or already loaded, false on error
	 */
	public static synchronized boolean loadRules(String filename, boolean forceReload) {
		if (rules != null && !forceReload) {
			return true;
		}

		if (filename != null && filename.length() > 0) {
			// Load from given filename
			rulesFileName = filename;
			Gson gson = new Gson();

			try {
				FileReader reader = new FileReader(rulesFileName);
				rules = gson.fromJson(reader, RuleSet.class);
				reader.close();

			} catch (FileNotFoundException e) {
				// If file not file construct a new empty rule set
				rules = new RuleSet();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			// Load from resource file
			Gson gson = new Gson();

			try {
				InputStreamReader reader = new InputStreamReader(
						RuleManager.class.getClassLoader().getResourceAsStream("ruleset.json"));
				rules = gson.fromJson(reader, RuleSet.class);
				reader.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return true;
	}

	/**
	 * Saves rules to the JSON file.
	 *
	 * @return true if rules saved successfully, false on error
	 */
	public static synchronized boolean saveRules() {
		if (rules == null) {
			return true;
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			FileWriter writer = new FileWriter(rulesFileName);
			gson.toJson(rules, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static void printRuleCounts() {
		if (rules == null)
			return;

		System.out.println("Top edge rule count: " + rules.topEdgeRules.size());
		System.out.println("Top corner rule count: " + rules.topCornerRules.size());
		System.out.println("Middle edge rule count: " + rules.middleEdgeRules.size());
		System.out.println("Corner swap rule count: " + rules.cornerSwapRules.size());
		System.out.println("Corner rotate rule count: " + rules.cornerRotateRules.size());
		System.out.println("Edge swap rule count: " + rules.edgeSwapRules.size());
	}

	public static synchronized int getTopEdgeRuleCount() {
		return rules.topEdgeRules.size();
	}

	public static synchronized int getTopCornerRuleCount() {
		return rules.topCornerRules.size();
	}

	public static synchronized int getMiddleEdgeRuleCount() {
		return rules.middleEdgeRules.size();
	}

	public static synchronized int getCornerSwapRuleCount() {
		return rules.cornerSwapRules.size();
	}

	public static synchronized int getCornerRotateRuleCount() {
		return rules.cornerRotateRules.size();
	}

	public static synchronized int getEdgeSwapRuleCount() {
		return rules.edgeSwapRules.size();
	}

	private static synchronized Rule findRule(List<Rule> ruleList, PieceState[] normalizedInitialState) {
		loadRules();

		for (Rule r : ruleList) {
			if (r.initialStateEquals(normalizedInitialState)) {
				return r;
			}
		}

		return null;
	}

	public static synchronized Rule findTopEdgeRule(PieceState normalizedInitialState) {
		loadRules();
		PieceState[] initalState = { normalizedInitialState };
		return findRule(rules.topEdgeRules, initalState);
	}

	public static synchronized Rule findTopCornerRule(PieceState normalizedInitialState) {
		loadRules();
		PieceState[] initalState = { normalizedInitialState };
		return findRule(rules.topCornerRules, initalState);
	}

	public static synchronized Rule findMiddleEdgeRule(PieceState normalizedInitialState) {
		loadRules();
		PieceState[] initalState = { normalizedInitialState };
		return findRule(rules.middleEdgeRules, initalState);
	}

	public static synchronized Rule findCornerSwapRule(PieceState[] normalizedInitialState) {
		loadRules();
		return findRule(rules.cornerSwapRules, normalizedInitialState);
	}

	public static synchronized Rule findCornerRotateRule(PieceState[] normalizedInitialState) {
		loadRules();
		return findRule(rules.cornerRotateRules, normalizedInitialState);
	}

	public static synchronized Rule findEdgeSwapRule(PieceState[] normalizedInitialState) {
		loadRules();
		return findRule(rules.edgeSwapRules, normalizedInitialState);
	}

	private static synchronized void addRule(List<Rule> ruleList, PieceState[] initialState, Move[] moves) {
		loadRules();

		if (moves.length == 0)
			throw new IllegalArgumentException("Rule must have at least one move.");

		// TODO: validate initial state

		Rule existing = findRule(ruleList, initialState);
		if (existing != null) {

			if (existing.getMoves().length <= moves.length) {
				return;
			} else {
				// shorter rule found
				existing.setMoves(moves);
			}
		} else {
			ruleList.add(new Rule(initialState, moves));
		}

		saveRules();
	}

	public static synchronized void addTopEdgeRule(PieceState normalizedInitialState, Move[] moves) {
		loadRules();
		PieceState[] initalState = { normalizedInitialState };
		addRule(rules.topEdgeRules, initalState, moves);
	}

	public static synchronized void addTopCornerRule(PieceState normalizedInitialState, Move[] moves) {
		loadRules();
		PieceState[] initalState = { normalizedInitialState };
		addRule(rules.topCornerRules, initalState, moves);
	}

	public static synchronized void addMiddleEdgeRule(PieceState normalizedInitialState, Move[] moves) {
		loadRules();
		PieceState[] initalState = { normalizedInitialState };
		addRule(rules.middleEdgeRules, initalState, moves);
	}

	public static synchronized void addCornerSwapRule(PieceState[] normalizedInitialState, Move[] moves) {
		loadRules();
		addRule(rules.cornerSwapRules, normalizedInitialState, moves);
	}

	public static synchronized void addCornerRotateRule(PieceState[] normalizedInitialState, Move[] moves) {
		loadRules();
		addRule(rules.cornerRotateRules, normalizedInitialState, moves);
	}

	public static synchronized void addEdgeSwapRule(PieceState[] normalizedInitialState, Move[] moves) {
		loadRules();
		addRule(rules.edgeSwapRules, normalizedInitialState, moves);
	}

	/**
	 * Converts a piece's colors to normalized colors based on the cube's current
	 * face colors. This allows pieces to be compared regardless of the cube's
	 * current orientation.
	 *
	 * @param c the cube to get face colors from
	 * @param p the piece to normalize
	 * @return a new Piece with normalized colors
	 */
	public static Piece getNormalizedPiece(RubiksCube c, Piece p) {

		Color xColor = p.getXColor();
		Color yColor = p.getYColor();
		Color zColor = p.getZColor();

		if (xColor != Color.NONE)
			xColor = RubiksCubeManager.getNormalizedColor(c.getFaceByColor(xColor));

		if (yColor != Color.NONE)
			yColor = RubiksCubeManager.getNormalizedColor(c.getFaceByColor(yColor));

		if (zColor != Color.NONE)
			zColor = RubiksCubeManager.getNormalizedColor(c.getFaceByColor(zColor));

		return new Piece(xColor, yColor, zColor);
	}

	/**
	 * Gets the normalized piece ID for a piece at the given location in the cube.
	 *
	 * @param c the cube containing the piece
	 * @param l the location of the piece
	 * @return the normalized piece ID
	 */
	public static int getNormalizedPieceID(RubiksCube c, Point3D l) {
		return getNormalizedPiece(c, c.getPieceByLocation(l)).getID();
	}

}
