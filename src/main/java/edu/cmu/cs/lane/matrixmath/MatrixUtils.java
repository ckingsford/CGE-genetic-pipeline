package edu.cmu.cs.lane.matrixmath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.ComplexFloat;
import org.jblas.ComplexFloatMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;
import org.jblas.FloatMatrix;
import org.jblas.MatrixFunctions;

import edu.cmu.cs.lane.datatypes.GFLasso.Coordinate;
import edu.cmu.cs.lane.offlinetasks.FileHelperTask;

public class MatrixUtils {

	public static float[] abs(float[] A) {
		float[] R = new float[A.length];
		for (int i = 0; i < A.length; ++i) {
			R[i] = Math.abs(A[i]);
		}
		return R;
	}

	public static FloatMatrix abs(FloatMatrix A) {
		FloatMatrix R = new FloatMatrix(A.rows, A.columns);
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				R.put(i, j, Math.abs(A.get(i, j)));
			}
		}
		return R;
	}

	public static FloatMatrix add(FloatMatrix X, FloatMatrix Y) {
		FloatMatrix R = copy(X);
		return R.add(Y);
	}

	public static float calculateNorm(FloatMatrix A) {
		FloatMatrix columnSum = FloatMatrix.zeros(A.columns);
		// The following lines should b
		FloatMatrix R = elementWiseSquared(A);
		for (int i = 0; i < A.columns; ++i) {
			columnSum.put(i, R.getColumn(i).sum());
		}
		return columnSum.max() * 2;
	}

	public static FloatMatrix copy(FloatMatrix A) {
		FloatMatrix R = new FloatMatrix(A.rows, A.columns);
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				R.put(i, j, A.get(i, j));
			}
		}
		return R;
	}

	public static Float correlationFunction(FloatMatrix x1, FloatMatrix x2,
			FloatMatrix CorrLengths, FloatMatrix tPowers) {
		FloatMatrix diff = x1.sub(x2);
		diff = MatrixFunctions.abs(diff);
		FloatMatrix clengths = FloatMatrix.diag(CorrLengths);
		FloatMatrix lhs = diff.mmul(clengths);
		tPowers = tPowers.sub((float) 1.0);
		FloatMatrix rhs = MatrixFunctions.powi(diff, tPowers);
		rhs = rhs.transpose();
		FloatMatrix minusLogAns = lhs.mmul(rhs);
		Float mMLans = minusLogAns.get(0);
		return (float) Math.exp(-mMLans);
	}

	public static FloatMatrix CorrMatrix(FloatMatrix tData,
			FloatMatrix CorrLengths, FloatMatrix tPowers) {
		int n = tData.rows;
		FloatMatrix ans = new FloatMatrix(n, n);
		Float tempCorrelation = null;
		for (int i = 0; i < n; i++) {
			ans.put(i, i, 1);
		}
		for (int j = 1; j < n; j++) {
			for (int k = 0; k < j; k++) {
				tempCorrelation = correlationFunction(tData.getRow(j),
						tData.getRow(k), CorrLengths, tPowers);
				ans.put(j, k, tempCorrelation);
				ans.put(k, j, tempCorrelation);
			}
		}
		return ans;
	}

	/*
	 * eigs Largest eigenvalues and eigenvectors of matrix return the k largest
	 * magnitude eigenvalues.
	 * 
	 * d = eigs(A) returns a vector of A's six largest magnitude eigenvalues. A
	 * must be a square matrix. A should be large and sparse, though eigs will
	 * work on full matrices as well.
	 */
	public static float[] symmetricEigs(FloatMatrix A, int k) {
		float[] d = symmetricEigs(A, k);
		float[] r = new float[k];
		for (int i = 0; i < k; i++) {
			r[i] = d[i];
		}
		return r;
	}

	public static float[] elementWiseSquared(float[] A) {
		float s[] = new float[A.length];
		for (int i = 0; i < A.length; ++i) {
			s[i] = (float) Math.pow(A[i], 2);
		}
		return s;
	}

	public static FloatMatrix elementWiseSquared(FloatMatrix A) {
		FloatMatrix B = new FloatMatrix(A.rows, A.columns);
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				B.put(i, j, (float) Math.pow(A.get(i, j), 2));
			}
		}
		return B;
	}

	public static FloatMatrix fastCorr(FloatMatrix A) {
		int n = A.rows;
		boolean left = true, right = true;
		for (float v : A.columnMeans().toArray()) {
			if (v >= 1e-5) {
				left = false;
				break;
			}
		}
		FloatMatrix v = FloatMatrix.zeros(1, A.columns);
		v = subtract(std(A), 1);
		for (int i = 0; i < v.columns; i++) {
			if (v.get(i, 0) >= 1e-5) {
				right = false;
				break;
			}
		}
		if (!(left && right)) {
			A = zScore(A);
		}

		FloatMatrix Atranmul = multiply(A.transpose(), A);
		FloatMatrix Atranmuldiv = mrdivide(Atranmul, (n - 1));

		return Atranmuldiv;
	}

	public static float[] find(FloatMatrix A) {
		int[] x = A.findIndices();
		float[] v = new float[x.length];
		for (int l = 0; l < x.length; l++) {
			v[l] = A.get((x[l] % A.columns), (x[l] / A.columns));
		}
		return v;
	}

	public static float[] find(FloatMatrix A, int[] x) {
		float[] v = new float[x.length];
		for (int l = 0; l < x.length; l++) {
			v[l] = A.get((x[l] % A.columns), (x[l] / A.columns));
		}
		return v;
	}

	public static float[] flatten(FloatMatrix A) {
		float[] r = new float[A.rows * A.columns];
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				if (A.rows > A.columns)
					r[(j * A.columns) + i] = A.get(i, j);
				else
					r[(i * A.rows) + j] = A.get(i, j);
			}
		}
		return r;
	}

	public static int getDensity(FloatMatrix A) {
		int d = 0;
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				if (A.get(i, j) != 0) {
					d++;
				}
			}
		}
		return d;
	}

	public static FloatMatrix hardThreshold(FloatMatrix A, float lambda) {
		FloatMatrix B = new FloatMatrix(A.rows, A.columns);
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				if (A.get(i, j) > lambda) {
					B.put(i, j, lambda);
				} else if (A.get(i, j) < -lambda) {
					B.put(i, j, -lambda);
				} else {
					B.put(i, j, A.get(i, j));
				}

			}
		}
		return B;
	}

	public static FloatMatrix loadAsciiFileWithHeadersToFloat(String filename)
			throws IOException {

		BufferedReader is = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename)));

		String line;
		int rows = 0;
		int columns = -1;
		while ((line = is.readLine()) != null) {
			if (rows == 0) {
				rows++;
				continue;
			}
			String[] elements = line.split("\\s+");
			int numElements = elements.length;
			if (elements[0].length() == 0) {
				numElements--;
			}
			if (elements[elements.length - 1].length() == 0) {
				numElements--;
			}
			numElements--;

			if (columns == -1) {
				columns = numElements;
			} else {
				if (columns != numElements) {
					System.err.println("Number of elements changes in line "
							+ line + ".");
				}
			}

			rows++;
		}
		is.close();
		rows--;

		// Go through file a second time process the actual data.
		is = new BufferedReader(new InputStreamReader(new FileInputStream(
				filename)));
		FloatMatrix result = new FloatMatrix(rows, columns);
		int r = 0;
		while ((line = is.readLine()) != null) {
			if (r == 0) {
				r++;
				continue;
			}
			String[] elements = line.split("\\s+");
			for (int c = 1; c < elements.length; c++)
				result.put(r - 1, c - 1, Float.valueOf(elements[c]));
			r++;
		}
		return result;
	}

	public static void ltThreshold(FloatMatrix A, float thresholdValue,
			float assignValue) {
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				float v = A.get(i, j);
				if (Math.abs(v) < thresholdValue) {
					v = assignValue;
				}
				A.put(i, j, v);
			}
		}

	}

	/*
	 * mean Average or mean value of array
	 * 
	 * M = mean(A,dim) returns the mean values for elements along the dimension
	 * of A specified by scalar dim. For matrices, mean(A,2) is a column vector
	 * containing the mean value of each row.
	 */
	public static FloatMatrix mean(FloatMatrix A, int dim) {
		if (dim == 1) {
			return A.columnMeans();
		} else if (dim == 2) {
			return A.rowMeans();
		}
		return null;
	}

	public static FloatMatrix mrdivide(FloatMatrix B, float a) {
		FloatMatrix R = new FloatMatrix(B.rows, B.columns);
		for (int i = 0; i < B.rows; ++i) {
			for (int j = 0; j < B.columns; ++j) {
				R.put(i, j, (B.get(i, j) / a));
			}
		}
		return R;
	}

	public static FloatMatrix mrdivide(FloatMatrix B, int a) {
		FloatMatrix R = new FloatMatrix(B.rows, B.columns);
		for (int i = 0; i < B.rows; ++i) {
			for (int j = 0; j < B.columns; ++j) {
				R.put(i, j, (B.get(i, j) / a));
			}
		}
		return R;
	}

	public static FloatMatrix multiply(float x, FloatMatrix Y) {
		return Y.mul(x);
	}

	public static float[] multiply(float[] X, float y) {
		float[] R = new float[X.length];
		for (int i = 0; i < X.length; ++i) {
			R[i] = X[i] * y;
		}
		return R;
	}

	public static FloatMatrix multiply(FloatMatrix X, float y) {
		return X.mul(y);
	}

	/*
	 * multiply Matrix multiplication
	 * 
	 * C = A*B is the linear algebraic product of the matrices A and B.
	 */
	public static FloatMatrix multiply(FloatMatrix X, FloatMatrix Y) {
		return X.mmul(Y);
		// FloatMatrix R = new FloatMatrix(X.rows, Y.columns);
		//
		// for (int j = 0; j < Y.columns; j++) {
		// for (int i = 0; i < X.rows; i++) {
		// float s = 0;
		// for (int k = 0; k < X.columns; k++) {
		// s += X.get(i, k) * Y.get(k, j);
		// }
		// R.put(i, j, s);
		// }
		// }
		// return R;
	}

	public static void print(float x, String s) {
		System.out.println(s + " =\n");
		System.out.println(x);
		System.out.println();
	}

	public static void print(float[] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.print(x[i] + "\t");
		}
		System.out.println();
	}

	public static void print(float[] x, String s) {
		System.out.println(s + " =\n");
		for (int i = 0; i < x.length; i++) {
			System.out.print(x[i] + "\n");
		}
		System.out.println();
	}

	public static void print(FloatMatrix X) {
		for (int i = 0; i < X.rows; ++i) {
			for (int j = 0; j < X.columns; ++j) {
				System.out.format("%10.4f", X.get(i, j));
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void print(FloatMatrix X, String s) {
		System.out.println(s + " =\n");
		for (int i = 0; i < X.rows; ++i) {
			for (int j = 0; j < X.columns; ++j) {
				System.out.format("%10.4f\t", X.get(i, j));
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void print(float[] x, String s, int e) {
		System.out.println(s + " =\n");
		System.out.println("   1.0e" + e + " *\n");
		for (int i = 0; i < x.length; i++) {
			System.out.format("%7.4f\t", x[i] * Math.pow(10, -e));
		}
		System.out.println();
	}

	public static void print(FloatMatrix X, String s, int e) {
		System.out.println(s + " =\n");
		System.out.println("   1.0e" + e + " *\n");
		for (int i = 0; i < X.rows; ++i) {
			for (int j = 0; j < X.columns; ++j) {
				System.out.format("%7.4f\t", X.get(i, j) * Math.pow(10, -e));
			}
			System.out.println();
		}
		System.out.println();
	}

	/*
	 * reshape Reshape array
	 * 
	 * B = reshape(A,m,n) returns the m-by-n matrix B whose elements are taken
	 * column-wise from A. An error results if A does not have m*n elements.
	 */
	public static FloatMatrix reshape(FloatMatrix A, int m, int n) {
		FloatMatrix B = new FloatMatrix(m, n);
		int k = 0;
		for (int j = 0; j < A.columns; ++j) {
			for (int i = 0; i < A.rows; ++i) {
				int x = k % m;
				int y = k / m;
				B.put(x, y, A.get(i, j));
				k++;
			}
		}
		return B;
	}

	public static float[] sign(float[] X) {
		float[] Y = new float[X.length];

		for (int i = 0; i < X.length; ++i) {
			Y[i] = Math.signum(X[i]);
		}

		return Y;
	}

	/*
	 * sign Signum function
	 * 
	 * Y = sign(X) returns an array Y the same size as X, where each element of
	 * Y is: 1 if the corresponding element of X is greater than zero 0 if the
	 * corresponding element of X equals zero -1 if the corresponding element of
	 * X is less than zero For nonzero complex X, sign(X) = X./abs(X).
	 */
	public static FloatMatrix sign(FloatMatrix X) {
		FloatMatrix Y = new FloatMatrix(X.rows, X.columns);

		for (int i = 0; i < X.rows; ++i) {
			for (int j = 0; j < X.columns; ++j) {
				Y.put(i, j, Math.signum(X.get(i, j)));
			}
		}

		return Y;
	}

	public static FloatMatrix softThreshold(FloatMatrix A, float lambda) {
		FloatMatrix B = new FloatMatrix(A.rows, A.columns);
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				if (A.get(i, j) > lambda) {
					B.put(i, j, A.get(i, j) - lambda);
				} else if (A.get(i, j) < -lambda) {
					B.put(i, j, A.get(i, j) + lambda);
				} else {
					B.put(i, j, 0);
				}

			}
		}
		return B;
	}

	/*
	 * sort Sort array elements in ascending or descending order
	 * 
	 * B = sort(A) Sorts each column of A.
	 */
	public static FloatMatrix sort(FloatMatrix A) {
		FloatMatrix B = FloatMatrix.zeros(A.rows, A.columns);

		for (int j = 0; j < A.columns; ++j) {
			float[] t = new float[A.rows];
			for (int i = 0; i < A.rows; ++i) {
				t[i] = A.get(i, j);
			}
			Arrays.sort(t);
			for (int i = 0; i < A.rows; ++i) {
				B.put(i, j, t[i]);
			}
		}

		return B;
	}

	public static FloatMatrix sparse(int[] i, int[] j, FloatMatrix s, int m,
			int n) {
		FloatMatrix C = FloatMatrix.zeros(m, n);
		for (int l = 0; l < i.length; l++) {
			C.put(i[l], j[l], s.get((l % s.rows), (l / s.rows)));
		}
		return C;
	}

	/*
	 * sqrt Square root
	 * 
	 * B = sqrt(X) returns the square root of each element of the array X.
	 */
	public static FloatMatrix sqrt(FloatMatrix X) {
		FloatMatrix Y = new FloatMatrix(X.rows, X.columns);

		for (int i = 0; i < X.rows; ++i) {
			for (int j = 0; j < X.columns; ++j) {
				Y.put(i, j, (float) Math.sqrt(X.get(i, j)));
			}
		}

		return Y;
	}

	public static float[] std(FloatMatrix a) {
		float[] s = new float[a.columns];
		int i = 0;
		for (float mean : a.columnMeans().toArray()) {
			float sd = (float) 0.0;
			for (int j = 0; j < a.rows; j++) {
				sd += Math.pow(mean - a.get(j, i), 2);
			}
			s[i] = (float) Math.sqrt(sd / (a.rows - 1));
			i++;
		}
		return s;
	}

	public static FloatMatrix subtract(float[] x, int y) {
		FloatMatrix A = FloatMatrix.zeros(1, x.length);
		for (int i = 0; i < x.length; i++) {
			A.put(0, i, x[i] - y);
		}
		return A;
	}

	/*
	 * subtract
	 */
	public static FloatMatrix subtract(FloatMatrix X, FloatMatrix Y) {
		FloatMatrix A = copy(X);
		return A.sub(Y);
	}

	public static float sum(float[] A) {
		float s = 0;
		for (int i = 0; i < A.length; ++i) {
			s += A[i];
		}
		return s;
	}

	/*
	 * sum Sum of array elements
	 * 
	 * B = sum(A) returns sums along different dimensions of an array. If A is
	 * floating point, that is float or single, B is accumulated natively, that
	 * is in the same class as A, and B has the same class as A. If A is not
	 * floating point, B is accumulated in float and B has class float.
	 */
	public static float[] sum(FloatMatrix A) {
		float[] b = new float[A.columns];

		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				if (i == 0) {
					b[j] = A.get(i, j);
				} else {
					b[j] += A.get(i, j);
				}
			}
		}
		return b;
	}

	public static FloatMatrix upperTriangle(FloatMatrix A) {
		FloatMatrix R = FloatMatrix.zeros(A.rows, A.columns);
		for (int i = 0; i < A.rows; ++i) {
			for (int j = i + 1; j < A.columns; ++j) {
				R.put(i, j, A.get(i, j));
			}
		}
		return R;
	}

	/*
	 * If X is a matrix, then Z is a matrix of the same size as X, and each
	 * column of Z has mean 0 and standard deviation 1.
	 */
	public static FloatMatrix zScore(FloatMatrix X) {
		FloatMatrix Z = FloatMatrix.zeros(X.rows, X.columns);
		float[] stddev = std(X);
		int j = 0;
		for (float mean : X.columnMeans().toArray()) {
			for (int i = 0; i < X.rows; i++) {
				if (stddev[j] == 0) {
					Z.put(i, j, 0);
				} else {
					Z.put(i, j, ((X.get(i, j) - mean) / stddev[j]));
				}
			}
			j++;
		}
		return Z;
	}
	public static double[] abs(double[] A) {
		double[] R = new double[A.length];
		for (int i = 0; i < A.length; ++i) {
			R[i] = Math.abs(A[i]);
		}
		return R;
	}

	public static DoubleMatrix abs(DoubleMatrix A) {
		DoubleMatrix R = new DoubleMatrix(A.rows, A.columns);
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				R.put(i, j, Math.abs(A.get(i, j)));
			}
		}
		return R;
	}

	public static DoubleMatrix add(DoubleMatrix X, DoubleMatrix Y) {
		DoubleMatrix R = copy(X);
		return R.add(Y);
	}	

	public static double calculateNorm(DoubleMatrix A) {
		DoubleMatrix columnSum = DoubleMatrix.zeros(A.columns);
		// The following lines should b
		DoubleMatrix R = elementWiseSquared(A);
		for (int i = 0; i < A.columns; ++i) {
			columnSum.put(i, R.getColumn(i).sum());
		}
		return columnSum.max() * 2;
	}

	public static DoubleMatrix copy(DoubleMatrix A) {
		DoubleMatrix R = new DoubleMatrix(A.rows, A.columns);
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				R.put(i, j, A.get(i, j));
			}
		}
		return R;
	}

	public static Double correlationFunction(DoubleMatrix x1, DoubleMatrix x2,
			DoubleMatrix CorrLengths, DoubleMatrix tPowers) {
		DoubleMatrix diff = x1.sub(x2);
		diff = MatrixFunctions.abs(diff);
		DoubleMatrix clengths = DoubleMatrix.diag(CorrLengths);
		DoubleMatrix lhs = diff.mmul(clengths);
		tPowers = tPowers.sub(1.0);
		DoubleMatrix rhs = MatrixFunctions.powi(diff, tPowers);
		rhs = rhs.transpose();
		DoubleMatrix minusLogAns = lhs.mmul(rhs);
		Double mMLans = minusLogAns.get(0);
		return Math.exp(-mMLans);
	}

	public static DoubleMatrix CorrMatrix(DoubleMatrix tData,
			DoubleMatrix CorrLengths, DoubleMatrix tPowers) {
		int n = tData.rows;
		DoubleMatrix ans = new DoubleMatrix(n, n);
		Double tempCorrelation = null;
		for (int i = 0; i < n; i++) {
			ans.put(i, i, 1);
		}
		for (int j = 1; j < n; j++) {
			for (int k = 0; k < j; k++) {
				tempCorrelation = correlationFunction(tData.getRow(j),
						tData.getRow(k), CorrLengths, tPowers);
				ans.put(j, k, tempCorrelation);
				ans.put(k, j, tempCorrelation);
			}
		}
		return ans;
	}

	/*
	 * eigs Largest eigenvalues and eigenvectors of matrix
	 * 
	 * d = eigs(A) returns a vector of A's six largest magnitude eigenvalues. A
	 * must be a square matrix. A should be large and sparse, though eigs will
	 * work on full matrices as well.
	 */
	public static double[] symmetricEigs(DoubleMatrix A) {
		double[] d = new double[A.rows];

		DoubleMatrix e = Eigen.symmetricEigenvalues(A);
		for (int i = 0; i < e.rows; ++i) {
			d[i] = e.get(e.rows - 1 - i, e.columns - 1);
		}
		return d;
	}

	/*
	 * eigs Largest eigenvalues and eigenvectors of matrix
	 * 
	 * d = eigs(A) returns a vector of A's six largest magnitude eigenvalues. A
	 * must be a square matrix. A should be large and sparse, though eigs will
	 * work on full matrices as well.
	 */
	public static double[] eigs(DoubleMatrix A) {
		double[] d = new double[A.rows];

		ComplexDoubleMatrix e = Eigen.eigenvalues(A);
		for (int i = 0; i < e.rows; ++i) {
			ComplexDouble c = e.get(i, 0);
			d[i] = c.real();
		}
		return d;
	}

	/*
	 * eigs Largest eigenvalues and eigenvectors of matrix
	 * 
	 * d = eigs(A) returns a vector of A's six largest magnitude eigenvalues. A
	 * must be a square matrix. A should be large and sparse, though eigs will
	 * work on full matrices as well.
	 */
	public static float[] eigs(FloatMatrix A) {
		float[] d = new float[A.rows];

		ComplexFloatMatrix e = Eigen.eigenvalues(A);
		for (int i = 0; i < e.rows; ++i) {
			ComplexFloat c = e.get(i, 0);
			d[i] = c.real();
		}
		return d;
	}

	/*
	 * eigs Largest eigenvalues and eigenvectors of matrix
	 * 
	 * d = eigs(A) returns a vector of A's six largest magnitude eigenvalues. A
	 * must be a square matrix. A should be large and sparse, though eigs will
	 * work on full matrices as well.
	 */
	public static double[] eigs(DoubleMatrix A, int k) {
		double[] d = new double[k];

		ComplexDoubleMatrix e = Eigen.eigenvalues(A);
		for (int i = 0; i < k; ++i) {
			ComplexDouble c = e.get(i, 0);
			d[i] = c.real();
		}
		return d;
	}

	/*
	 * eigs Largest eigenvalues and eigenvectors of matrix
	 * 
	 * d = eigs(A) returns a vector of A's six largest magnitude eigenvalues. A
	 * must be a square matrix. A should be large and sparse, though eigs will
	 * work on full matrices as well.
	 */
	public static float[] eigs(FloatMatrix A, int k) {
		float[] d = new float[k];

		ComplexFloatMatrix e = Eigen.eigenvalues(A);
		for (int i = 0; i < k; ++i) {
			ComplexFloat c = e.get(i, 0);
			d[i] = c.real();
		}
		return d;
	}

	/*
	 * eigs Largest eigenvalues and eigenvectors of matrix return the k largest
	 * magnitude eigenvalues.
	 * 
	 * d = eigs(A) returns a vector of A's six largest magnitude eigenvalues. A
	 * must be a square matrix. A should be large and sparse, though eigs will
	 * work on full matrices as well.
	 */
	public static double[] symmetricEigs(DoubleMatrix A, int k) {
		double[] d = symmetricEigs(A);
		double[] r = new double[k];
		for (int i = 0; i < k; i++) {
			r[i] = d[i];
		}
		return r;
	}

	public static double[] elementWiseSquared(double[] A) {
		double s[] = new double[A.length];
		for (int i = 0; i < A.length; ++i) {
			s[i] = Math.pow(A[i], 2);
		}
		return s;
	}

	public static DoubleMatrix elementWiseSquared(DoubleMatrix A) {
		DoubleMatrix B = new DoubleMatrix(A.rows, A.columns);
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				B.put(i, j, Math.pow(A.get(i, j), 2));
			}
		}
		return B;
	}

	public static DoubleMatrix fastCorr(DoubleMatrix A) {
		int n = A.rows;
		boolean left = true, right = true;
		for (double v : A.columnMeans().toArray()) {
			if (v >= 1e-5) {
				left = false;
				break;
			}
		}
		DoubleMatrix v = DoubleMatrix.zeros(1, A.columns);
		v = subtract(std(A), 1);
		for (int i = 0; i < v.columns; i++) {
			if (v.get(i, 0) >= 1e-5) {
				right = false;
				break;
			}
		}
		if (!(left && right)) {
			A = zScore(A);
		}

		DoubleMatrix Atranmul = multiply(A.transpose(), A);
		DoubleMatrix Atranmuldiv = mrdivide(Atranmul, (n - 1));

		return Atranmuldiv;
	}

	public static double[] find(DoubleMatrix A) {
		int[] x = A.findIndices();
		double[] v = new double[x.length];
		for (int l = 0; l < x.length; l++) {
			v[l] = A.get((x[l] % A.columns), (x[l] / A.columns));
		}
		return v;
	}

	public static double[] find(DoubleMatrix A, int[] x) {
		double[] v = new double[x.length];
		for (int l = 0; l < x.length; l++) {
			v[l] = A.get((x[l] % A.columns), (x[l] / A.columns));
		}
		return v;
	}

	public static double[] flatten(DoubleMatrix A) {
		double[] r = new double[A.rows * A.columns];
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				if (A.rows > A.columns)
					r[(j * A.columns) + i] = A.get(i, j);
				else
					r[(i * A.rows) + j] = A.get(i, j);
			}
		}
		return r;
	}

	public static int getDensity(DoubleMatrix A) {
		int d = 0;
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				if (A.get(i, j) != 0) {
					d++;
				}
			}
		}
		return d;
	}

	public static DoubleMatrix hardThreshold(DoubleMatrix A, double lambda) {
		DoubleMatrix B = new DoubleMatrix(A.rows, A.columns);
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				if (A.get(i, j) > lambda) {
					B.put(i, j, lambda);
				} else if (A.get(i, j) < -lambda) {
					B.put(i, j, -lambda);
				} else {
					B.put(i, j, A.get(i, j));
				}

			}
		}
		return B;
	}

	/*
	 * The ind2sub command determines the equivalent subscript values
	 * corresponding to a single index into an array.
	 * 
	 * [I,J] = ind2sub(siz,IND) returns the matrices I and J containing the
	 * equivalent row and column subscripts corresponding to each linear index
	 * in the matrix IND for a matrix of size siz. siz is a vector with ndim(A)
	 * elements (in this case, 2), where siz(1) is the number of rows and siz(2)
	 * is the number of columns.
	 */
	public static Coordinate[] ind2sub(int size, int[] locations) {
		Coordinate[] c = new Coordinate[locations.length];
		for (int l = 0; l < locations.length; l++) {
			c[l] = new Coordinate();
			c[l].setX(locations[l] % size);
			c[l].setY(locations[l] / size);
		}
		return c;
	}

	public static DoubleMatrix loadAsciiFileWithHeaders(String filename) throws IOException {
		BufferedReader is = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

		String line;
//		int rows = 0;
		int columns = -1;
		
		if  ((line = is.readLine()) != null) {

			String[] elements = line.split("\\s+");
			int numElements = elements.length;
			if (elements[0].length() == 0) {
				numElements--;
			}
			if (elements[elements.length - 1].length() == 0) {
				numElements--;
			}
			numElements--;

			if (columns == -1) {
				columns = numElements;
			} else {
				if (columns != numElements) {
					System.err.println("Number of elements changes in line " + line + ".");
				}
			}

			
		}
		is.close();
		
		int rows = FileHelperTask.getNumberOfLinesInFile(new InputStreamReader(new FileInputStream(filename)));
		rows --;
		// Go through file a second time process the actual data.
		is = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		DoubleMatrix result = new DoubleMatrix(rows, columns);
		int r = 0;
		while ((line = is.readLine()) != null) {
			if (r == 0) {
				r++;
				continue;
			}
			String[] elements = line.split("\\s+");
			for (int c = 1; c < elements.length; c++)
				result.put(r - 1, c - 1, Double.valueOf(elements[c]));
			r++;
		}
		return result;
	}

	public static void ltThreshold(DoubleMatrix A, double thresholdValue,
			double assignValue) {
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				double v = A.get(i, j);
				if (Math.abs(v) < thresholdValue) {
					v = assignValue;
				}
				A.put(i, j, v);
			}
		}

	}

	/*
	 * mean Average or mean value of array
	 * 
	 * M = mean(A,dim) returns the mean values for elements along the dimension
	 * of A specified by scalar dim. For matrices, mean(A,2) is a column vector
	 * containing the mean value of each row.
	 */
	public static DoubleMatrix mean(DoubleMatrix A, int dim) {
		if (dim == 1) {
			return A.columnMeans();
		} else if (dim == 2) {
			return A.rowMeans();
		}
		return null;
	}

	public static DoubleMatrix mrdivide(DoubleMatrix B, double a) {
		DoubleMatrix R = new DoubleMatrix(B.rows, B.columns);
		for (int i = 0; i < B.rows; ++i) {
			for (int j = 0; j < B.columns; ++j) {
				R.put(i, j, (B.get(i, j) / a));
			}
		}
		return R;
	}

	public static DoubleMatrix mrdivide(DoubleMatrix B, int a) {
		DoubleMatrix R = new DoubleMatrix(B.rows, B.columns);
		for (int i = 0; i < B.rows; ++i) {
			for (int j = 0; j < B.columns; ++j) {
				R.put(i, j, (B.get(i, j) / a));
			}
		}
		return R;
	}

	public static DoubleMatrix multiply(double x, DoubleMatrix Y) {
		return Y.mul(x);
	}

	public static double[] multiply(double[] X, double y) {
		double[] R = new double[X.length];
		for (int i = 0; i < X.length; ++i) {
			R[i] = X[i] * y;
		}
		return R;
	}

	public static DoubleMatrix multiply(DoubleMatrix X, double y) {
		return X.mul(y);
	}

	/*
	 * multiply Matrix multiplication
	 * 
	 * C = A*B is the linear algebraic product of the matrices A and B.
	 */
	public static DoubleMatrix multiply(DoubleMatrix X, DoubleMatrix Y) {
		return X.mmul(Y);
		// DoubleMatrix R = new DoubleMatrix(X.rows, Y.columns);
		//
		// for (int j = 0; j < Y.columns; j++) {
		// for (int i = 0; i < X.rows; i++) {
		// double s = 0;
		// for (int k = 0; k < X.columns; k++) {
		// s += X.get(i, k) * Y.get(k, j);
		// }
		// R.put(i, j, s);
		// }
		// }
		// return R;
	}

	public static void print(Coordinate[] c, String s) {
		System.out.println(s + " =\n");
		for (int i = 0; i < c.length; i++) {
			System.out.println(c[i].getX() + "," + c[i].getY());
		}
		System.out.println();
	}

	public static void print(double x, String s) {
		System.out.println(s + " =\n");
		System.out.println(x);
		System.out.println();
	}

	public static void print(double[] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.print(x[i] + "\t");
		}
		System.out.println();
	}

	public static void print(double[] x, String s) {
		System.out.println(s + " =\n");
		for (int i = 0; i < x.length; i++) {
			System.out.print(x[i] + "\n");
		}
		System.out.println();
	}

	public static void print(DoubleMatrix X) {
		for (int i = 0; i < X.rows; ++i) {
			for (int j = 0; j < X.columns; ++j) {
				System.out.format("%10.4f", X.get(i, j));
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void print(DoubleMatrix X, String s) {
		System.out.println(s + " =\n");
		for (int i = 0; i < X.rows; ++i) {
			for (int j = 0; j < X.columns; ++j) {
				System.out.format("%10.4f\t", X.get(i, j));
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void print(double[] x, String s, int e) {
		System.out.println(s + " =\n");
		System.out.println("   1.0e" + e + " *\n");
		for (int i = 0; i < x.length; i++) {
			System.out.format("%7.4f\t", x[i] * Math.pow(10, -e));
		}
		System.out.println();
	}

	public static void print(DoubleMatrix X, String s, int e) {
		System.out.println(s + " =\n");
		System.out.println("   1.0e" + e + " *\n");
		for (int i = 0; i < X.rows; ++i) {
			for (int j = 0; j < X.columns; ++j) {
				System.out.format("%7.4f\t", X.get(i, j) * Math.pow(10, -e));
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void print(int x) {
		System.out.println(x);
		System.out.println();
	}

	public static void print(int x, String s) {
		System.out.println(s + " =\n");
		System.out.println(x);
		System.out.println();
	}

	public static void print(int[] x) {
		for (int i = 0; i < x.length; i++) {
			System.out.print(x[i] + "\t");
		}
		System.out.println();
	}


	public static void print(int[] x, String s) {
		System.out.println(s + " =\n");
		for (int i = 0; i < x.length; i++) {
			System.out.println(x[i]);
		}
		System.out.println();
	}

	public static int[] randSample(int n, int k) {
		int[] array = new int[k];
		for (int i = 0; i < k; ++i) {
			array[i] = (int) (Math.random() * (n + 1));
		}
		return array;
	}

	/*
	 * reshape Reshape array
	 * 
	 * B = reshape(A,m,n) returns the m-by-n matrix B whose elements are taken
	 * column-wise from A. An error results if A does not have m*n elements.
	 */
	public static DoubleMatrix reshape(DoubleMatrix A, int m, int n) {
		DoubleMatrix B = new DoubleMatrix(m, n);
		int k = 0;
		for (int j = 0; j < A.columns; ++j) {
			for (int i = 0; i < A.rows; ++i) {
				int x = k % m;
				int y = k / m;
				B.put(x, y, A.get(i, j));
				k++;
			}
		}
		return B;
	}

	public static double[] sign(double[] X) {
		double[] Y = new double[X.length];

		for (int i = 0; i < X.length; ++i) {
			Y[i] = Math.signum(X[i]);
		}

		return Y;
	}

	/*
	 * sign Signum function
	 * 
	 * Y = sign(X) returns an array Y the same size as X, where each element of
	 * Y is: 1 if the corresponding element of X is greater than zero 0 if the
	 * corresponding element of X equals zero -1 if the corresponding element of
	 * X is less than zero For nonzero complex X, sign(X) = X./abs(X).
	 */
	public static DoubleMatrix sign(DoubleMatrix X) {
		DoubleMatrix Y = new DoubleMatrix(X.rows, X.columns);

		for (int i = 0; i < X.rows; ++i) {
			for (int j = 0; j < X.columns; ++j) {
				Y.put(i, j, Math.signum(X.get(i, j)));
			}
		}

		return Y;
	}

	public static DoubleMatrix softThreshold(DoubleMatrix A, double lambda) {
		DoubleMatrix B = new DoubleMatrix(A.rows, A.columns);
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				if (A.get(i, j) > lambda) {
					B.put(i, j, A.get(i, j) - lambda);
				} else if (A.get(i, j) < -lambda) {
					B.put(i, j, A.get(i, j) + lambda);
				} else {
					B.put(i, j, 0);
				}

			}
		}
		return B;
	}

	/*
	 * sort Sort array elements in ascending or descending order
	 * 
	 * B = sort(A) Sorts each column of A.
	 */
	public static DoubleMatrix sort(DoubleMatrix A) {
		DoubleMatrix B = DoubleMatrix.zeros(A.rows, A.columns);

		for (int j = 0; j < A.columns; ++j) {
			double[] t = new double[A.rows];
			for (int i = 0; i < A.rows; ++i) {
				t[i] = A.get(i, j);
			}
			Arrays.sort(t);
			for (int i = 0; i < A.rows; ++i) {
				B.put(i, j, t[i]);
			}
		}

		return B;
	}

	public static DoubleMatrix sparse(int[] i, int[] j, DoubleMatrix s, int m,
			int n) {
		DoubleMatrix C = DoubleMatrix.zeros(m, n);
		for (int l = 0; l < i.length; l++) {
			C.put(i[l], j[l], s.get((l % s.rows), (l / s.rows)));
		}
		return C;
	}

	/*
	 * sqrt Square root
	 * 
	 * B = sqrt(X) returns the square root of each element of the array X.
	 */
	public static DoubleMatrix sqrt(DoubleMatrix X) {
		DoubleMatrix Y = new DoubleMatrix(X.rows, X.columns);

		for (int i = 0; i < X.rows; ++i) {
			for (int j = 0; j < X.columns; ++j) {
				Y.put(i, j, Math.sqrt(X.get(i, j)));
			}
		}

		return Y;
	}

	public static double[] std(DoubleMatrix a) {
		double[] s = new double[a.columns];
		int i = 0;
		for (double mean : a.columnMeans().toArray()) {
			double sd = 0.0;
			for (int j = 0; j < a.rows; j++) {
				sd += Math.pow(mean - a.get(j, i), 2);
			}
			s[i] = Math.sqrt(sd / (a.rows - 1));
			i++;
		}
		return s;
	}

	public static DoubleMatrix subtract(double[] x, int y) {
		DoubleMatrix A = DoubleMatrix.zeros(1, x.length);
		for (int i = 0; i < x.length; i++) {
			A.put(0, i, x[i] - y);
		}
		return A;
	}

	/*
	 * subtract
	 */
	public static DoubleMatrix subtract(DoubleMatrix X, DoubleMatrix Y) {
		DoubleMatrix A = copy(X);
		return A.sub(Y);
	}

	public static double sum(double[] A) {
		double s = 0;
		for (int i = 0; i < A.length; ++i) {
			s += A[i];
		}
		return s;
	}

	/*
	 * sum Sum of array elements
	 * 
	 * B = sum(A) returns sums along different dimensions of an array. If A is
	 * floating point, that is double or single, B is accumulated natively, that
	 * is in the same class as A, and B has the same class as A. If A is not
	 * floating point, B is accumulated in double and B has class double.
	 */
	public static double[] sum(DoubleMatrix A) {
		double[] b = new double[A.columns];

		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				if (i == 0) {
					b[j] = A.get(i, j);
				} else {
					b[j] += A.get(i, j);
				}
			}
		}
		return b;
	}

	public static DoubleMatrix upperTriangle(DoubleMatrix A) {
		DoubleMatrix R = DoubleMatrix.zeros(A.rows, A.columns);
		for (int i = 0; i < A.rows; ++i) {
			for (int j = i + 1; j < A.columns; ++j) {
				R.put(i, j, A.get(i, j));
			}
		}
		return R;
	}

	/*
	 * If X is a matrix, then Z is a matrix of the same size as X, and each
	 * column of Z has mean 0 and standard deviation 1.
	 */
	public static DoubleMatrix zScore(DoubleMatrix X) {
		DoubleMatrix Z = DoubleMatrix.zeros(X.rows, X.columns);
		double[] stddev = std(X);
		int j = 0;
		for (double mean : X.columnMeans().toArray()) {
			for (int i = 0; i < X.rows; i++) {
				if (stddev[j] == 0) {
					Z.put(i, j, 0);
				} else {
					Z.put(i, j, ((X.get(i, j) - mean) / stddev[j]));
				}
			}
			j++;
		}
		return Z;
	}

	/*
	 * sum Sum of array elements
	 * 
	 * B = sum(A) returns sums along different dimensions of an array. If A is
	 * floating point, that is double or single, B is accumulated natively, that
	 * is in the same class as A, and B has the same class as A. If A is not
	 * floating point, B is accumulated in double and B has class double.
	 */
	public static double[] sum(DoubleMatrix A, int dim) {
		double[] b;

		if (dim == 2) {
			b = new double[A.rows];
			for (int j = 0; j < A.columns; ++j) {
				for (int i = 0; i < A.rows; ++i) {
					if (j == 0) {
						b[i] = A.get(i, j);
					} else {
						b[i] += A.get(i, j);
					}
				}
			}
		} else {
			b = new double[A.columns];
			for (int i = 0; i < A.rows; ++i) {
				for (int j = 0; j < A.columns; ++j) {
					if (i == 0) {
						b[j] = A.get(i, j);
					} else {
						b[j] += A.get(i, j);
					}
				}
			}
		}
		return b;
	}

	public static int nnz(DoubleMatrix A) {
		int nnz = 0;
		for (int i = 0; i < A.rows; ++i) {
			for (int j = 0; j < A.columns; ++j) {
				if (A.get(i, j) != 0) {
					nnz++;
				}
			}
		}
		return nnz;
	}

}
