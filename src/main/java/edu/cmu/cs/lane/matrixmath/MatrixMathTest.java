package edu.cmu.cs.lane.matrixmath;

import java.io.IOException;

import org.jblas.DoubleMatrix;
import org.junit.Assert;
import org.junit.Test;

public class MatrixMathTest {

	private static final double DELTA = 1e-15;
	private static final double DELTA_ML = 1e-3;

	@Test
	public void test() throws IOException {
		// A = [ -1 -2 5; 0 1 2; -9 9 -3 ];
		DoubleMatrix B = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test.txt");

		Assert.assertEquals(-1, B.get(0, 0), DELTA);
		Assert.assertEquals(-2, B.get(0, 1), DELTA);
		Assert.assertEquals(5, B.get(0, 2), DELTA);

		Assert.assertEquals(0, B.get(1, 0), DELTA);
		Assert.assertEquals(1, B.get(1, 1), DELTA);
		Assert.assertEquals(2, B.get(1, 2), DELTA);

		Assert.assertEquals(-9, B.get(2, 0), DELTA);
		Assert.assertEquals(9, B.get(2, 1), DELTA);
		Assert.assertEquals(-3, B.get(2, 2), DELTA);
	}

	@Test
	public void testAbsDoubleArray() {
		double[] a = { -1, -2, 5 };

		double[] r = MatrixUtils.abs(a);
		Assert.assertEquals(1, r[0], DELTA);
		Assert.assertEquals(2, r[1], DELTA);
		Assert.assertEquals(5, r[2], DELTA);

	}

	@Test
	public void testAbsDoubleMatrix() throws IOException {
		DoubleMatrix A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test.txt");

		DoubleMatrix B = MatrixUtils.abs(A);

		Assert.assertEquals(1, B.get(0, 0), DELTA);
		Assert.assertEquals(2, B.get(0, 1), DELTA);
		Assert.assertEquals(5, B.get(0, 2), DELTA);

		Assert.assertEquals(0, B.get(1, 0), DELTA);
		Assert.assertEquals(1, B.get(1, 1), DELTA);
		Assert.assertEquals(2, B.get(1, 2), DELTA);

		Assert.assertEquals(9, B.get(2, 0), DELTA);
		Assert.assertEquals(9, B.get(2, 1), DELTA);
		Assert.assertEquals(3, B.get(2, 2), DELTA);
	}

	@Test
	public void testAdd() throws IOException {
		// A = [ -1 -2 5; 0 1 2; -9 9 -3 ];
		DoubleMatrix A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test.txt");
		DoubleMatrix B = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		DoubleMatrix R = MatrixUtils.add(A, B);

		Assert.assertEquals(7, R.get(0, 0), DELTA);
		Assert.assertEquals(-1, R.get(0, 1), DELTA);
		Assert.assertEquals(11, R.get(0, 2), DELTA);

		Assert.assertEquals(3, R.get(1, 0), DELTA);
		Assert.assertEquals(6, R.get(1, 1), DELTA);
		Assert.assertEquals(9, R.get(1, 2), DELTA);

		Assert.assertEquals(-5, R.get(2, 0), DELTA);
		Assert.assertEquals(18, R.get(2, 1), DELTA);
		Assert.assertEquals(-1, R.get(2, 2), DELTA);
	}

	@Test
	public void testCopy() throws IOException {
		DoubleMatrix magic = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		DoubleMatrix R = MatrixUtils.copy(magic);

		Assert.assertEquals(8, R.get(0, 0), DELTA);
		Assert.assertEquals(1, R.get(0, 1), DELTA);
		Assert.assertEquals(6, R.get(0, 2), DELTA);

		Assert.assertEquals(3, R.get(1, 0), DELTA);
		Assert.assertEquals(5, R.get(1, 1), DELTA);
		Assert.assertEquals(7, R.get(1, 2), DELTA);

		Assert.assertEquals(4, R.get(2, 0), DELTA);
		Assert.assertEquals(9, R.get(2, 1), DELTA);
		Assert.assertEquals(2, R.get(2, 2), DELTA);
	}

	@Test
	public void testLoadAsciiFileWithHeaders() throws IOException {
		DoubleMatrix R = MatrixUtils
				.loadAsciiFileWithHeaders("src/test/resources/matrixtests/3x3-test-magic-headers.txt");

		Assert.assertEquals(8, R.get(0, 0), DELTA);
		Assert.assertEquals(1, R.get(0, 1), DELTA);
		Assert.assertEquals(6, R.get(0, 2), DELTA);

		Assert.assertEquals(3, R.get(1, 0), DELTA);
		Assert.assertEquals(5, R.get(1, 1), DELTA);
		Assert.assertEquals(7, R.get(1, 2), DELTA);

		Assert.assertEquals(4, R.get(2, 0), DELTA);
		Assert.assertEquals(9, R.get(2, 1), DELTA);
		Assert.assertEquals(2, R.get(2, 2), DELTA);
	}

	@Test
	public void testEigs() throws IOException {
		DoubleMatrix magic = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		double[] B = MatrixUtils.eigs(magic, 3);

		Assert.assertEquals(15, B[0], DELTA_ML);
		Assert.assertEquals(4.899, B[1], DELTA_ML);
		Assert.assertEquals(-4.899, B[2], DELTA_ML);

		B = MatrixUtils.eigs(magic, 2);
		Assert.assertEquals(15, B[0], DELTA_ML);
		Assert.assertEquals(4.899, B[1], DELTA_ML);

		B = MatrixUtils.eigs(magic, 1);
		Assert.assertEquals(15, B[0], DELTA_ML);
	}
	@Test
	public void testEigsDoubleMatrix() throws IOException {
		DoubleMatrix lehmer = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/4x4-lehmer.txt");

		double[] B;

		B = MatrixUtils.symmetricEigs(lehmer);
		Assert.assertEquals(2.5362, B[0], DELTA_ML);
		Assert.assertEquals(0.8482, B[1], DELTA_ML);
		Assert.assertEquals(0.4078, B[2], DELTA_ML);
		Assert.assertEquals(0.2078, B[3], DELTA_ML);
	}

	@Test
	public void testEigsDoubleMatrixInt() throws IOException {
		DoubleMatrix lehmer = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/4x4-lehmer.txt");

		double[] B;

		B = MatrixUtils.symmetricEigs(lehmer, 4);
		Assert.assertEquals(2.5362, B[0], DELTA_ML);
		Assert.assertEquals(0.8482, B[1], DELTA_ML);
		Assert.assertEquals(0.4078, B[2], DELTA_ML);
		Assert.assertEquals(0.2078, B[3], DELTA_ML);

		B = MatrixUtils.symmetricEigs(lehmer, 3);
		Assert.assertEquals(2.5362, B[0], DELTA_ML);
		Assert.assertEquals(0.8482, B[1], DELTA_ML);
		Assert.assertEquals(0.4078, B[2], DELTA_ML);

		B = MatrixUtils.symmetricEigs(lehmer, 2);
		Assert.assertEquals(2.5362, B[0], DELTA_ML);
		Assert.assertEquals(0.8482, B[1], DELTA_ML);

		B = MatrixUtils.symmetricEigs(lehmer, 1);
		Assert.assertEquals(2.5362, B[0], DELTA_ML);
	}

	@Test
	public void testElementWiseSquaredDoubleArray() {
		double[] a = {1,2,3};

		double[] r = MatrixUtils.elementWiseSquared(a);

		Assert.assertEquals(1, r[0], DELTA);
		Assert.assertEquals(4, r[1], DELTA);
		Assert.assertEquals(9, r[2], DELTA);
	}

	@Test
	public void testElementWiseSquaredDoubleMatrix() throws IOException {
		DoubleMatrix magic = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		DoubleMatrix B = MatrixUtils.elementWiseSquared(magic);

		Assert.assertEquals(64, B.get(0, 0), DELTA);
		Assert.assertEquals(1, B.get(0, 1), DELTA);
		Assert.assertEquals(36, B.get(0, 2), DELTA);

		Assert.assertEquals(9, B.get(1, 0), DELTA);
		Assert.assertEquals(25, B.get(1, 1), DELTA);
		Assert.assertEquals(49, B.get(1, 2), DELTA);

		Assert.assertEquals(16, B.get(2, 0), DELTA);
		Assert.assertEquals(81, B.get(2, 1), DELTA);
		Assert.assertEquals(4, B.get(2, 2), DELTA);
	}

	@Test
	public void testFlatten() throws IOException {
		DoubleMatrix magic = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		double[] r = MatrixUtils.flatten(magic);

		Assert.assertEquals(8, r[0], DELTA);
		Assert.assertEquals(1, r[1], DELTA);
		Assert.assertEquals(6, r[2], DELTA);

		Assert.assertEquals(3, r[3], DELTA);
		Assert.assertEquals(5, r[4], DELTA);
		Assert.assertEquals(7, r[5], DELTA);

		Assert.assertEquals(4, r[6], DELTA);
		Assert.assertEquals(9, r[7], DELTA);
		Assert.assertEquals(2, r[8], DELTA);
	}

	@Test
	public void testHardThreshold() throws IOException {
		// A = [ -1 -2 5; 0 1 2; -9 9 -3 ];
		DoubleMatrix A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test.txt");

		DoubleMatrix B = MatrixUtils.hardThreshold(A, 3);

		Assert.assertEquals(-1, B.get(0, 0), DELTA);
		Assert.assertEquals(-2, B.get(0, 1), DELTA);
		Assert.assertEquals(3, B.get(0, 2), DELTA);

		Assert.assertEquals(0, B.get(1, 0), DELTA);
		Assert.assertEquals(1, B.get(1, 1), DELTA);
		Assert.assertEquals(2, B.get(1, 2), DELTA);

		Assert.assertEquals(-3, B.get(2, 0), DELTA);
		Assert.assertEquals(3, B.get(2, 1), DELTA);
		Assert.assertEquals(-3, B.get(2, 2), DELTA);
	}

	@Test
	public void testMean() throws IOException {
		DoubleMatrix magic = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		DoubleMatrix B = MatrixUtils.mean(magic, 1);

		Assert.assertEquals(5, B.get(0, 0), DELTA);
		Assert.assertEquals(5, B.get(0, 1), DELTA);
		Assert.assertEquals(5, B.get(0, 2), DELTA);

		DoubleMatrix C = MatrixUtils.mean(magic, 2);

		Assert.assertEquals(5, C.get(0, 0), DELTA);
		Assert.assertEquals(5, C.get(1, 0), DELTA);
		Assert.assertEquals(5, C.get(2, 0), DELTA);
	}

	@Test
	public void testMrdivideDoubleMatrixDouble() throws IOException {
		DoubleMatrix magic = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		DoubleMatrix B = MatrixUtils.mrdivide(magic, 2.0);

		Assert.assertEquals(4, B.get(0, 0), DELTA_ML);
		Assert.assertEquals(0.5, B.get(0, 1), DELTA_ML);
		Assert.assertEquals(3, B.get(0, 2), DELTA_ML);

		Assert.assertEquals(1.5, B.get(1, 0), DELTA_ML);
		Assert.assertEquals(2.5, B.get(1, 1), DELTA_ML);
		Assert.assertEquals(3.5, B.get(1, 2), DELTA_ML);

		Assert.assertEquals(2, B.get(2, 0), DELTA_ML);
		Assert.assertEquals(4.5, B.get(2, 1), DELTA_ML);
		Assert.assertEquals(1, B.get(2, 2), DELTA_ML);
	}

	@Test
	public void testMrdivideDoubleMatrixInt() throws IOException {
		DoubleMatrix magic = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		DoubleMatrix B = MatrixUtils.mrdivide(magic, 2);

		Assert.assertEquals(4, B.get(0, 0), DELTA_ML);
		Assert.assertEquals(0.5, B.get(0, 1), DELTA_ML);
		Assert.assertEquals(3, B.get(0, 2), DELTA_ML);

		Assert.assertEquals(1.5, B.get(1, 0), DELTA_ML);
		Assert.assertEquals(2.5, B.get(1, 1), DELTA_ML);
		Assert.assertEquals(3.5, B.get(1, 2), DELTA_ML);

		Assert.assertEquals(2, B.get(2, 0), DELTA_ML);
		Assert.assertEquals(4.5, B.get(2, 1), DELTA_ML);
		Assert.assertEquals(1, B.get(2, 2), DELTA_ML);
	}

	@Test
	public void testMultiplyDoubleArrayDouble() {
		double[] a = {1,2,3};
		double b = 2;

		double[] r = MatrixUtils.multiply(a,b);

		Assert.assertEquals(2, r[0], DELTA);
		Assert.assertEquals(4, r[1], DELTA);
		Assert.assertEquals(6, r[2], DELTA);
	}

	@Test
	public void testMultiplyDoubleDoubleMatrix() throws IOException {
		DoubleMatrix magic = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		double d = 2.0;

		DoubleMatrix R = MatrixUtils.multiply(d, magic);

		Assert.assertEquals(16, R.get(0, 0), DELTA);
		Assert.assertEquals(2, R.get(0, 1), DELTA);
		Assert.assertEquals(12, R.get(0, 2), DELTA);

		Assert.assertEquals(6, R.get(1, 0), DELTA);
		Assert.assertEquals(10, R.get(1, 1), DELTA);
		Assert.assertEquals(14, R.get(1, 2), DELTA);

		Assert.assertEquals(8, R.get(2, 0), DELTA);
		Assert.assertEquals(18, R.get(2, 1), DELTA);
		Assert.assertEquals(4, R.get(2, 2), DELTA);
	}

	@Test
	public void testMultiplyDoubleMatrixDouble() throws IOException {
		DoubleMatrix magic = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		double d = 2.0;

		DoubleMatrix R = MatrixUtils.multiply(magic,d);

		Assert.assertEquals(16, R.get(0, 0), DELTA);
		Assert.assertEquals(2, R.get(0, 1), DELTA);
		Assert.assertEquals(12, R.get(0, 2), DELTA);

		Assert.assertEquals(6, R.get(1, 0), DELTA);
		Assert.assertEquals(10, R.get(1, 1), DELTA);
		Assert.assertEquals(14, R.get(1, 2), DELTA);

		Assert.assertEquals(8, R.get(2, 0), DELTA);
		Assert.assertEquals(18, R.get(2, 1), DELTA);
		Assert.assertEquals(4, R.get(2, 2), DELTA);
	}

	@Test
	public void testMultiplyDoubleMatrixDoubleMatrix() throws IOException {
		// A = [ -1 -2 5; 0 1 2; -9 9 -3 ];
		DoubleMatrix A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test.txt");
		DoubleMatrix B = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		DoubleMatrix R = MatrixUtils.multiply(A, B);

		Assert.assertEquals(6, R.get(0, 0), DELTA);
		Assert.assertEquals(34, R.get(0, 1), DELTA);
		Assert.assertEquals(-10, R.get(0, 2), DELTA);

		Assert.assertEquals(11, R.get(1, 0), DELTA);
		Assert.assertEquals(23, R.get(1, 1), DELTA);
		Assert.assertEquals(11, R.get(1, 2), DELTA);

		Assert.assertEquals(-57, R.get(2, 0), DELTA);
		Assert.assertEquals(9, R.get(2, 1), DELTA);
		Assert.assertEquals(3, R.get(2, 2), DELTA);
		DoubleMatrix a = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x2-mult.txt");
		DoubleMatrix b = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/2x3-mult.txt");

		R = MatrixUtils.multiply(a, b);

		Assert.assertEquals(9, R.get(0, 0), DELTA);
		Assert.assertEquals(12, R.get(0, 1), DELTA);
		Assert.assertEquals(15, R.get(0, 2), DELTA);

		Assert.assertEquals(25, R.get(1, 0), DELTA);
		Assert.assertEquals(32, R.get(1, 1), DELTA);
		Assert.assertEquals(39, R.get(1, 2), DELTA);

		Assert.assertEquals(41, R.get(2, 0), DELTA);
		Assert.assertEquals(52, R.get(2, 1), DELTA);
		Assert.assertEquals(63, R.get(2, 2), DELTA);

		R = MatrixUtils.multiply(b, a);

		Assert.assertEquals(76, R.get(0, 0), DELTA);
		Assert.assertEquals(100, R.get(0, 1), DELTA);

		Assert.assertEquals(22, R.get(1, 0), DELTA);
		Assert.assertEquals(28, R.get(1, 1), DELTA);

	}

	@Test
	public void testReshape() throws IOException {
		// A = [ 1, 2, 3; 4, 5, 6 ];
		DoubleMatrix A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x2-test.txt");

		DoubleMatrix B = MatrixUtils.reshape(A, 3, 2);

		Assert.assertEquals(1, B.get(0, 0), DELTA);
		Assert.assertEquals(5, B.get(0, 1), DELTA);

		Assert.assertEquals(4, B.get(1, 0), DELTA);
		Assert.assertEquals(3, B.get(1, 1), DELTA);

		Assert.assertEquals(2, B.get(2, 0), DELTA);
		Assert.assertEquals(6, B.get(2, 1), DELTA);
	}

	@Test
	public void testSignDoubleArray() {
		double[] a = { -1, -2, 5 };

		double[] r = MatrixUtils.sign(a);
		Assert.assertEquals(-1, r[0], DELTA);
		Assert.assertEquals(-1, r[1], DELTA);
		Assert.assertEquals(1, r[2], DELTA);
	}

	@Test
	public void testSignDoubleMatrix() throws IOException {
		DoubleMatrix A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test.txt");

		DoubleMatrix B = MatrixUtils.sign(A);

		Assert.assertEquals(-1, B.get(0, 0), DELTA);
		Assert.assertEquals(-1, B.get(0, 1), DELTA);
		Assert.assertEquals(1, B.get(0, 2), DELTA);

		Assert.assertEquals(0, B.get(1, 0), DELTA);
		Assert.assertEquals(1, B.get(1, 1), DELTA);
		Assert.assertEquals(1, B.get(1, 2), DELTA);

		Assert.assertEquals(-1, B.get(2, 0), DELTA);
		Assert.assertEquals(1, B.get(2, 1), DELTA);
		Assert.assertEquals(-1, B.get(2, 2), DELTA);
	}

	@Test
	public void testSoftThreshold() throws IOException {
		// A = [ -1 -2 5; 0 1 2; -9 9 -3 ];
		DoubleMatrix A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test.txt");

		DoubleMatrix B = MatrixUtils.softThreshold(A, 3);

		Assert.assertEquals(0, B.get(0, 0), DELTA);
		Assert.assertEquals(0, B.get(0, 1), DELTA);
		Assert.assertEquals(2, B.get(0, 2), DELTA);

		Assert.assertEquals(0, B.get(1, 0), DELTA);
		Assert.assertEquals(0, B.get(1, 1), DELTA);
		Assert.assertEquals(0, B.get(1, 2), DELTA);

		Assert.assertEquals(-6, B.get(2, 0), DELTA);
		Assert.assertEquals(6, B.get(2, 1), DELTA);
		Assert.assertEquals(0, B.get(2, 2), DELTA);
	}

	@Test
	public void testSort() throws IOException {
		DoubleMatrix A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test.txt");

		int b = MatrixUtils.nnz(A);

		Assert.assertEquals(8, b, DELTA);

	}

	@Test
	public void testNnz() throws IOException {
		DoubleMatrix A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test.txt");
		int r = MatrixUtils.nnz(A);
		Assert.assertEquals(8, r, DELTA);

		A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");
		r = MatrixUtils.nnz(A);
		Assert.assertEquals(9, r, DELTA);
	}

	@Test
	public void testSqrt() throws IOException {
		// A = [ 0 1 4; 9 16 25; 36 49 64 ];
		DoubleMatrix A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-squares.txt");

		DoubleMatrix B = MatrixUtils.sqrt(A);

		Assert.assertEquals(0, B.get(0, 0), DELTA);
		Assert.assertEquals(1, B.get(0, 1), DELTA);
		Assert.assertEquals(2, B.get(0, 2), DELTA);

		Assert.assertEquals(3, B.get(1, 0), DELTA);
		Assert.assertEquals(4, B.get(1, 1), DELTA);
		Assert.assertEquals(5, B.get(1, 2), DELTA);

		Assert.assertEquals(6, B.get(2, 0), DELTA);
		Assert.assertEquals(7, B.get(2, 1), DELTA);
		Assert.assertEquals(8, B.get(2, 2), DELTA);
	}

	@Test
	public void testStd() throws IOException {
		// A = [ 1 2 4 4; 3 5 5 2; 2 2 3 9 ];
		DoubleMatrix A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/4x3-test.txt");

		double[] b = MatrixUtils.std(A);

		Assert.assertEquals(1, b[0], DELTA_ML);
		Assert.assertEquals(1.7321, b[1], DELTA_ML);
		Assert.assertEquals(1, b[2], DELTA_ML);
		Assert.assertEquals(3.6056, b[3], DELTA_ML);
	}

	@Test
	public void testSubtractDoubleArrayInt() {
		double[] x = { 3, 2, 1 };

		DoubleMatrix R = MatrixUtils.subtract(x, 1);

		Assert.assertEquals(2, R.get(0, 0), DELTA);
		Assert.assertEquals(1, R.get(0, 1), DELTA);
		Assert.assertEquals(0, R.get(0, 2), DELTA);
	}

	@Test
	public void testSubtractDoubleMatrixDoubleMatrix() throws IOException {
		// A = [ -1 -2 5; 0 1 2; -9 9 -3 ];
		DoubleMatrix A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test.txt");
		DoubleMatrix B = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		DoubleMatrix R = MatrixUtils.subtract(A, B);

		Assert.assertEquals(-9, R.get(0, 0), DELTA);
		Assert.assertEquals(-3, R.get(0, 1), DELTA);
		Assert.assertEquals(-1, R.get(0, 2), DELTA);

		Assert.assertEquals(-3, R.get(1, 0), DELTA);
		Assert.assertEquals(-4, R.get(1, 1), DELTA);
		Assert.assertEquals(-5, R.get(1, 2), DELTA);

		Assert.assertEquals(-13, R.get(2, 0), DELTA);
		Assert.assertEquals(0, R.get(2, 1), DELTA);
		Assert.assertEquals(-5, R.get(2, 2), DELTA);
	}

	@Test
	public void testSumDoubleArray() {
		double[] x = { 3, 2, 1 };

		double y = MatrixUtils.sum(x);

		Assert.assertEquals(6, y, DELTA);
	}

	@Test
	public void testSumDoubleMatrix() throws IOException {
		// A = [ -1 -2 5; 0 1 2; -9 9 -3 ];
		DoubleMatrix A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test.txt");

		double[] b = MatrixUtils.sum(A);
		Assert.assertEquals(-10, b[0], DELTA);
		Assert.assertEquals(8.0, b[1], DELTA);
		Assert.assertEquals(4.0, b[2], DELTA);

		b = MatrixUtils.sum(A, 1);
		Assert.assertEquals(-10, b[0], DELTA);
		Assert.assertEquals(8.0, b[1], DELTA);
		Assert.assertEquals(4.0, b[2], DELTA);

		double[] c = MatrixUtils.sum(A, 2);
		Assert.assertEquals(2.0, c[0], DELTA);
		Assert.assertEquals(3.0, c[1], DELTA);
		Assert.assertEquals(-3.0, c[2], DELTA);

		// A = [ 1 2 4 4; 3 5 5 2; 2 2 3 9 ];
		A = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/4x3-test.txt");

		b = MatrixUtils.sum(A, 1);
		Assert.assertEquals(6, b[0], DELTA);
		Assert.assertEquals(9, b[1], DELTA);
		Assert.assertEquals(12, b[2], DELTA);
		Assert.assertEquals(15, b[3], DELTA);

		c = MatrixUtils.sum(A, 2);
		Assert.assertEquals(11, c[0], DELTA);
		Assert.assertEquals(15, c[1], DELTA);
		Assert.assertEquals(16, c[2], DELTA);
	}

	@Test
	public void testUpperTriangle() throws IOException {
		// MATLAB: triu(magic,1)
		DoubleMatrix magic = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		DoubleMatrix R = MatrixUtils.upperTriangle(magic);

		Assert.assertEquals(0, R.get(0, 0), DELTA);
		Assert.assertEquals(1, R.get(0, 1), DELTA);
		Assert.assertEquals(6, R.get(0, 2), DELTA);

		Assert.assertEquals(0, R.get(1, 0), DELTA);
		Assert.assertEquals(0, R.get(1, 1), DELTA);
		Assert.assertEquals(7, R.get(1, 2), DELTA);

		Assert.assertEquals(0, R.get(2, 0), DELTA);
		Assert.assertEquals(0, R.get(2, 1), DELTA);
		Assert.assertEquals(0, R.get(2, 2), DELTA);
	}

	@Test
	public void testTranspose() throws IOException {
		DoubleMatrix magic = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		DoubleMatrix R = magic.transpose();

		Assert.assertEquals(8, R.get(0, 0), DELTA);
		Assert.assertEquals(3, R.get(0, 1), DELTA);
		Assert.assertEquals(4, R.get(0, 2), DELTA);

		Assert.assertEquals(1, R.get(1, 0), DELTA);
		Assert.assertEquals(5, R.get(1, 1), DELTA);
		Assert.assertEquals(9, R.get(1, 2), DELTA);

		Assert.assertEquals(6, R.get(2, 0), DELTA);
		Assert.assertEquals(7, R.get(2, 1), DELTA);
		Assert.assertEquals(2, R.get(2, 2), DELTA);
	}

	@Test
	public void testZScore() throws IOException {
		DoubleMatrix magic = DoubleMatrix
				.loadAsciiFile("src/test/resources/matrixtests/3x3-test-magic.txt");

		DoubleMatrix B = MatrixUtils.zScore(magic);

		Assert.assertEquals(1.1339, B.get(0, 0), DELTA_ML);
		Assert.assertEquals(-1, B.get(0, 1), DELTA_ML);
		Assert.assertEquals(0.3780, B.get(0, 2), DELTA_ML);

		Assert.assertEquals(-0.7559, B.get(1, 0), DELTA_ML);
		Assert.assertEquals(0, B.get(1, 1), DELTA_ML);
		Assert.assertEquals(0.7559, B.get(1, 2), DELTA_ML);

		Assert.assertEquals(-0.3780, B.get(2, 0), DELTA_ML);
		Assert.assertEquals(1, B.get(2, 1), DELTA_ML);
		Assert.assertEquals(-1.1339, B.get(2, 2), DELTA_ML);
	}

}
