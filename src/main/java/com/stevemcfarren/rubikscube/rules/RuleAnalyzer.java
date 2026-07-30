package com.stevemcfarren.rubikscube.rules;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.stevemcfarren.rubikscube.Color;
import com.stevemcfarren.rubikscube.Piece;
import com.stevemcfarren.rubikscube.Point3D;
import com.stevemcfarren.rubikscube.RubiksCube;
import com.stevemcfarren.rubikscube.RubiksCube.Face;
import com.stevemcfarren.rubikscube.RubiksCubeManager;

public class RuleAnalyzer {
	private RuleSet ruleFinderRules = null;

	public RuleAnalyzer() {
		Gson gson = new Gson();

		try {
			FileReader reader = new FileReader("RubiksCube_RuleFinder_RuleSet_12.json");
			ruleFinderRules = gson.fromJson(reader, RuleSet.class);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void analyzeTopEdgeRules() {
		System.out.println("Top Edge Rules");
		System.out.println("-> Count: " + ruleFinderRules.topEdgeRules.size());
		int minSeq = 100;
		int maxSeq = 0;
		for (Rule r : ruleFinderRules.topEdgeRules) {
			minSeq = Math.min(minSeq, r.getMoves().length);
			maxSeq = Math.max(maxSeq, r.getMoves().length);
		}
		System.out.println("-> Min length: " + minSeq);		
		System.out.println("-> Max length: " + maxSeq);		
	}

	private void analyzeTopCornerRules() {
		System.out.println("Top Corner Rules");
		System.out.println("-> Count: " + ruleFinderRules.topCornerRules.size());
		int minSeq = 100;
		int maxSeq = 0;
		for (Rule r : ruleFinderRules.topCornerRules) {
			minSeq = Math.min(minSeq, r.getMoves().length);
			maxSeq = Math.max(maxSeq, r.getMoves().length);
		}
		System.out.println("-> Min length: " + minSeq);		
		System.out.println("-> Max length: " + maxSeq);		
	}

	private void analyzeMiddleEdgeRules() {
		System.out.println("Middle Edge Rules");
		System.out.println("-> Count: " + ruleFinderRules.middleEdgeRules.size());
		int minSeq = 100;
		int maxSeq = 0;
		for (Rule r : ruleFinderRules.middleEdgeRules) {
			minSeq = Math.min(minSeq, r.getMoves().length);
			maxSeq = Math.max(maxSeq, r.getMoves().length);
		}
		System.out.println("-> Min length: " + minSeq);		
		System.out.println("-> Max length: " + maxSeq);		
	}

	private void analyzeCornerSwapRules() {
		System.out.println("Corner Swap Rules");
		RubiksCube solvedCube = RubiksCubeManager.getSolvedCube();
		int minSeq = 100;
		int maxSeq = 0;
		int leftCornerSwaps = 0;
		int frontCornerSwaps = 0;
		int oppositeCornerSwaps = 0;
		int threeCornerSwaps = 0;
		for (Rule r : ruleFinderRules.cornerSwapRules) {
			List<Point3D> unmoved = new ArrayList<Point3D>();
			if (r.getInitialState().length != 4) {
				System.out.println("WARNING: Expected initial state to have all four corners.");
			}
			for (int i=0; i<4; i++) {
			//for (PieceState p : r.getInitialState()) {
				PieceState state = r.getInitialState()[i];

				if (state.pieceID == solvedCube.getPieceByLocation(state.location).getID()) {
					unmoved.add(state.location);
				}

			}
			
			if (!unmoved.contains(new Point3D(1, -1, -1))) {
				System.out.println("WARNING: By convention, corner swaps are not supposed to impact back right.");
			}
			
			if (unmoved.size() == 2) {
				if (unmoved.contains(new Point3D(-1, -1, 1))) {
					oppositeCornerSwaps++;
				}
				else {
					if (unmoved.contains(new Point3D(-1, -1, -1))) {
						frontCornerSwaps++;
					}
					else {
						leftCornerSwaps++;
					}
				}
			}
			
			if (unmoved.size() == 1) {
				threeCornerSwaps++;
			}
			
			minSeq = Math.min(minSeq, r.getMoves().length);
			maxSeq = Math.max(maxSeq, r.getMoves().length);
		}

		System.out.println("-> Total Count: " + ruleFinderRules.cornerSwapRules.size());
		System.out.println("-> Left Corner Swaps: " + leftCornerSwaps);		
		System.out.println("-> Front Corner Swaps: " + frontCornerSwaps);		
		System.out.println("-> Opposite Corner Swaps: " + oppositeCornerSwaps);		
		System.out.println("-> Three Corner Swaps: " + threeCornerSwaps);		
		System.out.println("-> Min length: " + minSeq);		
		System.out.println("-> Max length: " + maxSeq);		
	}

	private void analyzeCornerRotates() {
		System.out.println("Corner Rotate Rules");
		RubiksCube solvedCube = RubiksCubeManager.getSolvedCube();
		int minSeq = 100;
		int maxSeq = 0;
		int oneCorner = 0;
		int twoCorner = 0;
		int threeCorner = 0;
		int fourCorner = 0;
		Color bottomColor = RubiksCubeManager.getNormalizedColor(Face.BOTTOM);
		for (Rule r : ruleFinderRules.cornerRotateRules) {
			int[] rotations = { 0, 0, 0, 0 };
			int rotatedPieces = 0;
			if (r.getInitialState().length != 4) {
				System.out.println("Expected initial state to have all four corners.");
			}
			for (int i=0; i<4; i++) {
			//for (PieceState p : r.getInitialState()) {
				PieceState state = r.getInitialState()[i];
				Piece piece = state.getPiece();
				if (!piece.equals(solvedCube.getPieceByLocation(state.location))) {
					rotatedPieces++;
					if (piece.getZColor() == bottomColor)
						rotations[i] = 1;
					else
						rotations[i] = -1;
					if (state.location.x != state.location.z)
						rotations[i] *= -1;
				}
			}
			int rotationTotal = rotations[0] + rotations[1] + rotations[2] + rotations[3];
			System.out.println(String.format("->Rotations: %d, %d, %d, %d; Total = %d", rotations[0], rotations[1], rotations[2], rotations[3], rotationTotal));

			switch(rotatedPieces) {
			case 1:
				oneCorner++;
				break;
			case 2:
				twoCorner++;
				break;
			case 3:
				threeCorner++;
				break;
			case 4:
				fourCorner++;
				break;
			}

			minSeq = Math.min(minSeq, r.getMoves().length);
			maxSeq = Math.max(maxSeq, r.getMoves().length);
		}

		System.out.println("-> Total Count: " + ruleFinderRules.cornerRotateRules.size());
		System.out.println("-> One Corner Rotates: " + oneCorner);		
		System.out.println("-> Two Corner Rotates: " + twoCorner);		
		System.out.println("-> Three Corner Rotates: " + threeCorner);		
		System.out.println("-> Four Corner Rotates: " + fourCorner);		
		System.out.println("-> Min length: " + minSeq);		
		System.out.println("-> Max length: " + maxSeq);		
	}

	private void analyzeEdgeSwapRules() {
		int minSeq = 100;
		int maxSeq = 0;
		for (Rule r : ruleFinderRules.edgeSwapRules) {
			minSeq = Math.min(minSeq, r.getMoves().length);
			maxSeq = Math.max(maxSeq, r.getMoves().length);
		}
		
		System.out.println("Edge Swap Rules");
		System.out.println("-> Count: " + ruleFinderRules.edgeSwapRules.size());
		System.out.println("-> Min length: " + minSeq);		
		System.out.println("-> Max length: " + maxSeq);		
	}

	
	public static void main(String[] args) {
		RuleAnalyzer processor = new RuleAnalyzer();

		processor.analyzeTopEdgeRules();
		processor.analyzeTopCornerRules();
		processor.analyzeMiddleEdgeRules();
		processor.analyzeCornerSwapRules();
		processor.analyzeCornerRotates();
		processor.analyzeEdgeSwapRules();
	}

}
