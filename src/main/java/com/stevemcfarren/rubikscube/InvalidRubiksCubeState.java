/**
 * 
 */
package com.stevemcfarren.rubikscube;

/**
 * 
 */
public class InvalidRubiksCubeState extends RuntimeException {

	public InvalidRubiksCubeState() {
		super();
	}

	public InvalidRubiksCubeState(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidRubiksCubeState(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidRubiksCubeState(String message) {
		super(message);
	}

	public InvalidRubiksCubeState(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
