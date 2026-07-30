package com.stevemcfarren.rubikscube.rules;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for all solving rules used by the Rubik's Cube solver.
 * This class serves as a data structure for serializing and deserializing rules to/from JSON.
 * 
 * @see RuleManager for accessing and managing rules
 */
public class RuleSet {
	/** Rules for solving top edge pieces */
	protected List<Rule> topEdgeRules;
	/** Rules for solving top corner pieces */
	protected List<Rule> topCornerRules;
	/** Rules for solving middle edge pieces */
	protected List<Rule> middleEdgeRules;
	/** Rules for swapping bottom corners */
	protected List<Rule> cornerSwapRules;
	/** Rules for rotating bottom corners in place*/
	protected List<Rule> cornerRotateRules;
	/** Rules for swapping bottom corners */
	protected List<Rule> edgeSwapRules;
	
	/**
	 * Constructs an empty RuleSet with initialized rule lists.
	 */
	protected RuleSet() {
		topEdgeRules = new ArrayList<Rule>();
		topCornerRules = new ArrayList<Rule>();
		middleEdgeRules = new ArrayList<Rule>();
		cornerSwapRules = new ArrayList<Rule>();
		cornerRotateRules = new ArrayList<Rule>();
		edgeSwapRules = new ArrayList<Rule>();
	}

}
