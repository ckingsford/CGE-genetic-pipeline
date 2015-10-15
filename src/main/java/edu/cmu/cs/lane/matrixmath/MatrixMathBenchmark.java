package edu.cmu.cs.lane.matrixmath;

import java.io.IOException;

import org.jblas.DoubleMatrix;

public class MatrixMathBenchmark {

	public static void main(String[] args) throws IOException {
		long tic, toc;
		System.out.println("size\tload\teigs\tchk");
		// 100, 250, 500, 1000, 2500, 5000, 10000, 25000
		int[] sizes = { 100 };
		for (int s : sizes) {
			System.out.print(s + "\t");
			String file = "/Backend/samples/data/matrix." + s
					+ "-" + s + ".space.txt";

			tic = System.currentTimeMillis();
			DoubleMatrix XX = DoubleMatrix.loadAsciiFile(file);
			toc = System.currentTimeMillis() - tic;
			System.out.print((double) toc / 1000 + "\t");

			tic = System.currentTimeMillis();
			double[] eigs = MatrixUtils.symmetricEigs(XX);
			toc = System.currentTimeMillis() - tic;
			System.out.print((double) toc / 1000 + "\t");

			System.out.println(eigs[0]);
		}
	}
}
