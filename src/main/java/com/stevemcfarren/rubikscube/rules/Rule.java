package com.stevemcfarren.rubikscube.rules;

import com.stevemcfarren.rubikscube.Move;

/**
 * Represents a solving rule for moving individual pieces into place without
 * disturbing other pieces that are already solved. A rules defines the initial
 * state of the target pieces and the sequence of moves required to place them
 * in the correct position.
 */
public class Rule {
	private final PieceState[] initialState;
	private Move[] moves;

	/**
	 * Gets the initial piece states that this rule applies to.
	 *
	 * @return the array of piece states defining when this rule should be applied
	 */
	public PieceState[] getInitialState() {
		return this.initialState;
	}

	/**
	 * Gets the rotations for each corner performed by the rule.
	 *
	 * @return the array of piece states defining when this rule should be applied
	 */
	public int[] getRotations() {
		// TODO
		return null;
	}

	/**
	 * Gets the sequence of moves that solve this corner swap configuration.
	 *
	 * @return the list of moves to apply
	 */
	public Move[] getMoves() {
		return this.moves;
	}

	/**
	 * Updates the rule if a shorter sequence of moves is found.
	 * 
	 * @param newMoves
	 */
	public void setMoves(Move[] newMoves) {
		this.moves = newMoves;
	}

	/**
	 * Constructs a new CornerRotateRule with the given initial state, final state
	 * and moves.
	 *
	 * @param initialState the piece states this rule applies to
	 * @param finalState   the piece states of the four corners after the sequence
	 *                     of moves
	 * @param moves        the sequence of moves for this rule
	 */
	public Rule(PieceState[] initialState, Move[] moves) {
		if (initialState.length == 0 || initialState.length > 4) {
			throw new IllegalArgumentException("Initial state should include  1-4 pieces.");
		}
		if (moves.length == 0) {
			throw new IllegalArgumentException("Rule must have at least 1 move.");
		}

		this.initialState = initialState;
		this.moves = moves;
	}
	
	public Rule(PieceState initialState, Move[] moves) {
		PieceState[] state = { initialState };
		this(state, moves);
	}

	/**
	 * Checks if the given piece states match this rule's initial state.
	 *
	 * @param givenState the piece states to compare
	 * @return true if the states match, false otherwise
	 */
	public boolean initialStateEquals(PieceState[] givenState) {
		if (initialState.length != givenState.length) {
			return false;
		}

		for (PieceState ruleState : initialState) {
			boolean found = false;
			for (PieceState state : givenState) {
				if (ruleState.equals(state)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}
}
