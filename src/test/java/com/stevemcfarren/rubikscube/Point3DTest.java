package com.stevemcfarren.rubikscube;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.stevemcfarren.rubikscube.Point3D.Axis;

class Point3DTest {

	@Test
	void testPoint3D() {
		Point3D pt = new Point3D(1, 2, 3);

		assertEquals(1, pt.x);
		assertEquals(2, pt.y);
		assertEquals(3, pt.z);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	void testPoint3D_Equals() {
		Point3D p1 = new Point3D(1,1,1);
		Point3D p2 = new Point3D(1,1,1);
		Point3D p3 = new Point3D(-1,1,1);
		
		assertTrue(p1.equals(p1), "Object should equal itself.");
		assertFalse(p1.equals(null), "Object should not equal null.");
		assertTrue(p1.equals(p2), "P1 and P2 should be equal.");
		assertFalse(p1.equals(p3), "P1 should not equal P3.");
		assertFalse(p1.equals(new String("1,1,1")), "Point3D object should not equal String object.");		
	}

	@Test
	void testPoint3D_HashCode() {
		Point3D p1 = new Point3D(1,1,1);
		Point3D p2 = new Point3D(1,1,1);
		Point3D p3 = new Point3D(-1,1,1);

		assertEquals(p1.hashCode(), p2.hashCode());
		assertNotEquals(p1.hashCode(), p3.hashCode());
	}
	
	@Test
	void testPoint3D_ToString() {
		Point3D pt = new Point3D(4, 5, 6);
		assertEquals("(4, 5, 6)", pt.toString());
	}

	@Test
	void testPoint3D_Rotate() {
		Point3D pt = new Point3D(1, 1, 1);
		Point3D newPt = null;

		newPt = pt.rotate(90, Axis.Z);
		assertEquals(new Point3D(-1, 1, 1), newPt);
		newPt = pt.rotate(-90, Axis.Z);
		assertEquals(new Point3D(1, -1, 1), newPt);
		newPt = pt.rotate(180, Axis.Z);
		assertEquals(new Point3D(-1, -1, 1), newPt);

		newPt = pt.rotate(90, Axis.X);
		assertEquals(new Point3D(1, 1, -1), newPt);
		newPt = pt.rotate(-90, Axis.X);
		assertEquals(new Point3D(1, -1, 1), newPt);
		newPt = pt.rotate(180, Axis.X);
		assertEquals(new Point3D(1, -1, -1), newPt);

		newPt = pt.rotate(90, Axis.Y);
		assertEquals(new Point3D(1, 1, -1), newPt);
		newPt = pt.rotate(-90, Axis.Y);
		assertEquals(new Point3D(-1, 1, 1), newPt);
		newPt = pt.rotate(180, Axis.Y);
		assertEquals(new Point3D(-1, 1, -1), newPt);
	}
}
