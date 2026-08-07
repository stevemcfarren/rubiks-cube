package com.stevemcfarren.rubikscube.rules;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stevemcfarren.rubikscube.Move;
import com.stevemcfarren.rubikscube.RubiksCubeManager;

/**
 * Record structure for the RuleFinder configuration data.
 * 
 * @param ruleSetFilename - Filename of the rule set JSON file any found rules
 *                        will be written to
 * @param minLength       - Minimum sequence length RuleFinder will use to find
 *                        rules.
 * @param maxLength       - Maximum sequence length RuleFinder will use to find
 *                        rules. The number of sequences grows exponentially
 *                        with maxLengh. Testing shows roughly 5 minute run time
 *                        with maxLength of 9 and 10x increase in run time with
 *                        each increase in maxLength.
 * @param initialSequence - The number of initial moves given to each thread as
 *                        the starting moves of the sequence. This number
 *                        defines the size of each batch as well as the depth of
 *                        search (and therefore the runtime) of each thread.
 * @param batchSize       - The number of batches to process.
 * @param nexSeqId        - The ID of the starting sequence of the next batch to
 *                        process. Use this number to start a new run where a
 *                        previous run left off or to divide the job amongst
 *                        multiple process. Must be greater than or equal to 0
 *                        and less than 12^(initialSequence-1).
 */
record RuleFinderConfig(String ruleSetFilename, int minLength, int maxLength, int initialSequence, int batchSize,
		int nextSeqId) {
}

/**
 * 
 */
public class RuleFinder {

	private static final int DEFAULT_MAX_SEQ = 10;
	private static final int DEFAULT_MIN_SEQ = 1;
	private static final int DEFAULT_INIT_SEQ = 4;
	private static final int DEFAULT_BATCHES_PER_RUN = 5;
	private static final String RULESET_FILENAME = "RubiksCube_RuleFinder_RuleSet_default.json";

	public static void processNextBatch(RuleFinderConfig config) {
		if (config.nextSeqId() == 0) {
			// Start by finding short sequences in a single thread
			RuleFinderThread finder = new RuleFinderThread(null, 1, config.initialSequence(), null);
			finder.run();
		}

		// Now divide and conquer
		RuleFinderThread[] threads = new RuleFinderThread[12];

		int maxSeq = Math.powExact(12, config.initialSequence() - 1);
		int endSeq = Math.min(config.nextSeqId() + config.batchSize(), maxSeq);

		for (int curSeq = config.nextSeqId(); curSeq < endSeq; curSeq++) {
			System.out.println("Starting sequence " + curSeq + " of " + (endSeq - 1));
			List<Move> moves = new ArrayList<Move>();
			boolean wastedMove = false;
			long startTime = System.currentTimeMillis();

			// Build list of initial moves based on batch number. The batch number is a
			// base-12 number with 0 representing the left-most path down the tree and
			// moving left to right across the tree as batch number increases.
			int tempSeq = curSeq;
			for (int i = config.initialSequence() - 2; i >= 0; i--) {
				int divisor = Math.powExact(12, i);
				Move nextMove = RubiksCubeManager.ALLMOVES[tempSeq / divisor];
				if (RuleHelper.isMoveWasted(moves, nextMove)) {
					wastedMove = true;
					break;
				}
				moves.add(nextMove);
				tempSeq = (int) tempSeq % divisor;
			}

			if (wastedMove) {
				continue;
			}

			CountDownLatch latch = new CountDownLatch(12);

			for (int m = 0; m < 12; m++) {
				Move nextMove = RubiksCubeManager.ALLMOVES[m];
				if (RuleHelper.isMoveWasted(moves, nextMove)) {
					latch.countDown();
					continue;
				}
				moves.add(nextMove);

				threads[m] = new RuleFinderThread(moves, config.minLength(), config.maxLength(), latch);
				Thread t1 = new Thread(threads[m]);
				t1.start();

				moves.removeLast();
			}

			try {
				if (!latch.await(360, TimeUnit.SECONDS)) {
					System.out.println("Timeout waiting for rule finder. Seq = " + curSeq);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long elapsedTime = System.currentTimeMillis() - startTime;
			System.out.println("Sequence time: " + elapsedTime + "ms");
		}
	}

	private static RuleFinderConfig loadConfig() {
		Gson gson = new Gson();
		RuleFinderConfig config = null;

		try {
			FileReader reader = new FileReader("RuleFinderConfig.json");
			config = gson.fromJson(reader, RuleFinderConfig.class);
			reader.close();
		} catch (FileNotFoundException e) {
			config = new RuleFinderConfig(RULESET_FILENAME, DEFAULT_MIN_SEQ, DEFAULT_MAX_SEQ, DEFAULT_INIT_SEQ,
					DEFAULT_BATCHES_PER_RUN, 0);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return config;
	}

	private static void saveConfig(RuleFinderConfig config) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			FileWriter writer = new FileWriter("RuleFinderConfig.json");
			gson.toJson(config, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();

		RuleFinderConfig config = loadConfig();
		RuleManager.loadRules(config.ruleSetFilename(), false);

		processNextBatch(config);

		int maxSeq = Math.powExact(12, config.initialSequence() - 1);

		int nextSeqId = Math.min(config.nextSeqId() + config.batchSize(), maxSeq);
		config = new RuleFinderConfig(config.ruleSetFilename(), config.minLength(), config.maxLength(),
				config.initialSequence(), config.batchSize(), nextSeqId);

		saveConfig(config);

		System.out.println("============================");
		System.out.println("Completed " + (nextSeqId - 1) + " of " + (maxSeq - 1));
		RuleManager.printRuleCounts();

		long totalElapsed = (System.currentTimeMillis() - start) / 1000;
		int hours = (int) totalElapsed / 3600;
		int minutes = (int) (totalElapsed / 60) % 60;
		int seconds = (int) totalElapsed % 60;
		System.out.println("Batch processing time: " + hours + ":" + minutes + ":" + seconds);
	}

}
