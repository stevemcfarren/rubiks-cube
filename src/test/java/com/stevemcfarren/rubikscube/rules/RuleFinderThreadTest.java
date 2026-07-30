package com.stevemcfarren.rubikscube.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stevemcfarren.rubikscube.Move;
import com.stevemcfarren.rubikscube.RubiksCube.Face;

class RuleFinderThreadTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		Path path = Paths.get("output");
        
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            System.err.println("Failed to create directory: " + e.getMessage());
        }

        RuleManager.loadRules("output/TestRuleSet.json", true);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			FileWriter writer = new FileWriter("output/TestRuleSet.json");
			gson.toJson(new RuleSet(), writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		RuleManager.loadRules(null, true);
	}

	@Test
	void test() {
		CountDownLatch latch = new CountDownLatch(1);

		RuleFinderThread finder = new RuleFinderThread(null, 1, 6, latch);
		Thread t1 = new Thread(finder);
		t1.start();

		try {
			if (!latch.await(300, TimeUnit.SECONDS)) {
				System.out.println("Timeout waiting for rule finder.");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		RuleManager.printRuleCounts();
		
		assertTrue(finder.getStatus());
		assertEquals(23, RuleManager.getTopEdgeRuleCount());
		assertEquals(21, RuleManager.getTopCornerRuleCount());
		assertEquals(2, RuleManager.getMiddleEdgeRuleCount());
		assertEquals(0, RuleManager.getCornerSwapRuleCount());
		assertEquals(0, RuleManager.getCornerRotateRuleCount());
		assertEquals(0, RuleManager.getEdgeSwapRuleCount());
	}

	
	@Test
	void test2() {
		CountDownLatch latch = new CountDownLatch(1);

		List<Move> moves = new ArrayList<Move>();
		moves.add(new Move(Face.BOTTOM, -90));
		moves.add(new Move(Face.BOTTOM, -90));
		moves.add(new Move(Face.FRONT, 90));
		
		RuleFinderThread finder = new RuleFinderThread(moves, 1, 10, latch);
		Thread t1 = new Thread(finder);
		t1.start();

		try {
			if (!latch.await(300, TimeUnit.SECONDS)) {
				System.out.println("Timeout waiting for rule finder.");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		RuleManager.printRuleCounts();

		assertTrue(finder.getStatus());
		assertEquals(23, RuleManager.getTopEdgeRuleCount());
		assertEquals(23, RuleManager.getTopCornerRuleCount());
		assertEquals(9, RuleManager.getMiddleEdgeRuleCount());
		assertEquals(4, RuleManager.getCornerSwapRuleCount());
		assertEquals(4, RuleManager.getCornerRotateRuleCount());
		assertEquals(0, RuleManager.getEdgeSwapRuleCount());
	}
	
}
