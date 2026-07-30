package com.stevemcfarren.rubikscube;

import com.stevemcfarren.rubikscube.RubiksCube.DisplayData;

public class TestResult {
	public final String description;
	public final DisplayData rubikscube;
	
	public TestResult(String description, DisplayData cube) {
		this.description = description;
		this.rubikscube = cube;
	}
}
