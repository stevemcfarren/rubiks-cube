package com.stevemcfarren.rubikscube;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import com.stevemcfarren.rubikscube.Point3D.Axis;


class PieceTest {

	@Test
	final void testPiece() {
		Piece center = new Piece(Color.NONE, Color.WHITE, Color.NONE);
		assertEquals(1, center.getID());
		assertEquals(Color.NONE, center.getXColor());
		assertEquals(Color.WHITE, center.getYColor());
		assertEquals(Color.NONE, center.getZColor());
		
		Piece edge = new Piece(Color.NONE, Color.WHITE, Color.BLUE);
		assertEquals(5, edge.getID());
		assertEquals(Color.NONE, edge.getXColor());
		assertEquals(Color.WHITE, edge.getYColor());
		assertEquals(Color.BLUE, edge.getZColor());

		Piece corner = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		assertEquals(7, corner.getID());
		assertEquals(Color.RED, corner.getXColor());
		assertEquals(Color.WHITE, corner.getYColor());
		assertEquals(Color.BLUE, corner.getZColor());

		Piece copy = new Piece(corner);
		assertEquals(7, copy.getID());
		assertTrue(copy.equals(corner));
	}
	

	@SuppressWarnings("unlikely-arg-type")
	@Test
	final void testPiece_equals() {
		Piece p1 = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		Piece p2 = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		Piece p3 = new Piece(Color.RED, Color.NONE, Color.BLUE);
		assertTrue(p1.equals(p1), "Object should equal itself.");
		assertFalse(p1.equals(null), "Object should not equal null.");
		assertTrue(p1.equals(p2), "P1 and P2 should be equal.");
		assertFalse(p1.equals(p3), "P1 should not equal P3.");
		assertFalse(p1.equals(new Point3D(1,1,1)), "Piece object should not equal Point3D object.");		
	}
	
	@Test
	final void testPiece_toString() {
		Piece p1 = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		assertEquals("(RED, WHITE, BLUE)", p1.toString());
	}

	@Test
	final void testPiece_hash() {
		Piece p1 = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		Piece p2 = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		Piece p3 = new Piece(Color.RED, Color.NONE, Color.BLUE);
		
		assertEquals(p1.hashCode(), p2.hashCode());
		assertNotEquals(p1.hashCode(), p3.hashCode());
	}

	@Test
	final void testPiece_InvalidColor() {
		try {
		    new Piece(Color.NONE, Color.NONE, Color.NONE);
		    fail( "Expected IllegalArgumentException" );
		} catch (IllegalArgumentException e) {
			assertEquals("At least one color must not be 'None'.", e.getMessage());
		}
		try {
		    new Piece(Color.WHITE, Color.WHITE, Color.NONE);
		    fail( "Expected IllegalArgumentException" );
		} catch (IllegalArgumentException e) {
			assertEquals("Two sides cannot be the same color: WHITE, WHITE, NONE", e.getMessage());
		}
		try {
		    new Piece(Color.WHITE, Color.NONE, Color.WHITE);
		    fail( "Expected IllegalArgumentException" );
		} catch (IllegalArgumentException e) {
			assertEquals("Two sides cannot be the same color: WHITE, NONE, WHITE", e.getMessage());
		}
		try {
		    new Piece(Color.NONE, Color.WHITE, Color.WHITE);
		    fail( "Expected IllegalArgumentException" );
		} catch (IllegalArgumentException e) {
			assertEquals("Two sides cannot be the same color: NONE, WHITE, WHITE", e.getMessage());
		}
	}
	
	@Test
	final void testPiece_RotateInvalidAngle() {
		Piece p = new Piece(Color.BLUE, Color.WHITE, Color.RED);
		
		try {
			// Invalid center point.
			p.rotate(Axis.X, 45);
		    fail( "Expected IllegalArgumentException" );
		} catch (IllegalArgumentException e) {
			assertEquals("'Angle' multiple of 90 between -270 and 270: 45", e.getMessage());
		}
		try {
			// Invalid center point.
			p.rotate(Axis.X, -360);
		    fail( "Expected IllegalArgumentException" );
		} catch (IllegalArgumentException e) {
			assertEquals("'Angle' multiple of 90 between -270 and 270: -360", e.getMessage());
		}
		try {
			// Invalid center point.
			p.rotate(Axis.X, 360);
		    fail( "Expected IllegalArgumentException" );
		} catch (IllegalArgumentException e) {
			assertEquals("'Angle' multiple of 90 between -270 and 270: 360", e.getMessage());
		}
	}
	
	@Test
	final void testPiece_RotateFaces() {
		Piece test = null;
		Piece expected = null;

		// Rotate on X axis, clockwise
		test = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		expected = new Piece(Color.RED, Color.BLUE, Color.WHITE);
		test.rotate(Axis.X, 90);
		assertEquals(expected, test);

		// Rotate on X axis, counter-clockwise
		test = new Piece(Color.RED, Color.WHITE, Color.GREEN);
		expected = new Piece(Color.RED, Color.GREEN, Color.WHITE);
		test.rotate(Axis.X, -90);
		assertEquals(expected, test);
		
		// Rotate on Y axis, clockwise
		test = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		expected = new Piece(Color.BLUE, Color.WHITE, Color.RED);
		test.rotate(Axis.Y, 90);
		assertEquals(expected, test);
		
		// Rotate on Y axis, counter-clockwise
		test = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		expected = new Piece(Color.BLUE, Color.WHITE, Color.RED);
		test.rotate(Axis.Y, -90);
		assertEquals(expected, test);
		
		// Rotate on Z axis, clockwise
		test = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		expected = new Piece(Color.WHITE, Color.RED, Color.BLUE);
		test.rotate(Axis.Z, 90);
		assertEquals(expected, test);
		
		// Rotate on Z axis, counter-clockwise
		test = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		expected = new Piece(Color.WHITE, Color.RED, Color.BLUE);
		test.rotate(Axis.Z, -90);
		assertEquals(expected, test);
	}

	@Test
	final void testPiece_RotateNoChange() {
		Piece test = null;
		Piece expected = null;

		test = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		expected = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		test.rotate(Axis.X, 180);
		assertEquals(expected, test);

		test = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		expected = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		test.rotate(Axis.X, 90);
		test.rotate(Axis.X, 90);
		test.rotate(Axis.X, 90);
		test.rotate(Axis.X, 90);
		assertEquals(expected, test);

	}

	@Test
	final void testHasColor() {
		Piece test = new Piece(Color.RED, Color.WHITE, Color.BLUE);
		assertTrue(test.hasColor(Color.RED));
		assertFalse(test.hasColor(Color.ORANGE));
		assertFalse(test.hasColor(Color.YELLOW));
		assertFalse(test.hasColor(Color.GREEN));
		assertTrue(test.hasColor(Color.BLUE));
		assertTrue(test.hasColor(Color.WHITE));
		assertFalse(test.hasColor(Color.NONE));
	}
}
