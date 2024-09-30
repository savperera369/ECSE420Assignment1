package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

public class MatrixMultiplication {
	
	private static final int NUMBER_THREADS = 2;
	private static final int MATRIX_SIZE = 4000;

	public static void main(String[] args) {
		// Generate two random matrices, same size
		double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		double[][] b = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
		long startTime;
		long endTime;

		// startTime = System.currentTimeMillis();
		// double[][] sequentialResult = sequentialMultiplyMatrix(a, b);
		// endTime = System.currentTimeMillis();
		// long sequentialTime = endTime - startTime;

		startTime = System.currentTimeMillis();
		double[][] parallelResult = parallelMultiplyMatrix(a, b);	
		endTime = System.currentTimeMillis();
		long parallelTime = endTime - startTime;

		System.out.println("Parallel Time: " + parallelTime);
		// if (Arrays.deepEquals(sequentialResult, parallelResult)) {
		// 	System.out.println("Both the sequential and parallel multiplications return the same reponse.\n");
		// 	System.out.println("Sequential Time: " + sequentialTime + "\n");
		// 	System.out.println("Parallel Time: " + parallelTime);
		// } else {
		// 	System.out.println("The sequential and parallel executions return different results");
		// }
	}
	
	/**
	 * Returns the result of a sequential matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
	public static double[][] sequentialMultiplyMatrix(double[][] a, double[][] b) {
		double[][] resultMatrix = new double[MATRIX_SIZE][MATRIX_SIZE];

		for (int i=0; i<MATRIX_SIZE; i++) {
			for (int j=0; j<MATRIX_SIZE; j++) {
				resultMatrix[i][j] = 0;
				for (int k=0; k<MATRIX_SIZE; k++) {
					resultMatrix[i][j] += a[i][k] * b[k][j];
				}
			}
		}

		return resultMatrix;
	}
	
	/**
	 * Returns the result of a concurrent matrix multiplication
	 * The two matrices are randomly generated
	 * @param a is the first matrix
	 * @param b is the second matrix
	 * @return the result of the multiplication
	 * */
	public static double[][] parallelMultiplyMatrix(double[][] a, double[][] b) {
		double[][] resultMatrix = new double[MATRIX_SIZE][MATRIX_SIZE];
		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

		try {
			for (int i=0; i<MATRIX_SIZE; i++) {
				for (int j=0; j<MATRIX_SIZE; j++) {
					executor.execute(new ParallelMultiplyMatrices(i, j, a, b, resultMatrix));
				}
			}
	
			executor.shutdown();
			executor.awaitTermination(MATRIX_SIZE, TimeUnit.SECONDS);
		} catch (Exception e) {
			System.out.println("exception");
		}

		return resultMatrix;
	}

	static class ParallelMultiplyMatrices implements Runnable {
		private int row;
		private int col;
		private double[][] a;
		private double[][] b;
		private double[][] result;

		public ParallelMultiplyMatrices (int row, int col, double[][] a, double[][] b, double[][] result) {
			this.row = row;
			this.col = col;
			this.a = a;
			this.b = b;
			this.result = result;
		}

		public void run() {
			int colLength = b[0].length;
			this.result[row][col] = 0;
			for (int k=0; k<colLength; k++) {
				this.result[row][col] += a[row][k] * b[k][col];
			}
		}
	}
        
	/**
	 * Populates a matrix of given size with randomly generated integers between 0-10.
	 * @param numRows number of rows
	 * @param numCols number of cols
	 * @return matrix
	 */
	private static double[][] generateRandomMatrix (int numRows, int numCols) {
		double matrix[][] = new double[numRows][numCols];
		for (int row = 0 ; row < numRows ; row++ ) {
			for (int col = 0 ; col < numCols ; col++ ) {
				matrix[row][col] = (double) ((int) (Math.random() * 10.0));
			}
		}
		return matrix;
    }
	
}


