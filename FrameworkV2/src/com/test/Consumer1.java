package com.test;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import com.test.*;
import org.apache.log4j.Logger;

public class Consumer1 extends Thread {
	static Consumer1 objConsumer = null;
	LinkedBlockingQueue<Segment> segQueue = new LinkedBlockingQueue<Segment>();
	static final int maxQueueSize = 100000;
	static Logger logger = Logger.getLogger(Consumer1.class);

	public static Consumer1 getInstance() {
		if (objConsumer == null) {
			objConsumer = new Consumer1();
			objConsumer.setName("Consumer-1");

		}
		return objConsumer;
	}

	@Override
	public void run() {

		while (true) {
			int[][] changeCalOut = new int[4][255];
			int[] diffSummed = new int[4];

			Segment objSegment = segQueue.poll();

			if (objSegment != null) {
				for (int i = 0; i < 4; i++) {
					int[] segChunk = objSegment.segmentArr[i];
					int n = segChunk.length;

					int[] iqr_q1_q3 = IQR(segChunk.clone(), n);

					// System.out.println("the IQR is: " + iqr_q1_q3[0]);

					LinkedBlockingQueue<Integer> outliers = findOutliers(iqr_q1_q3[0], segChunk, iqr_q1_q3[1],
							iqr_q1_q3[2]);

					while (!outliers.isEmpty()) {
						int arrPlace = outliers.poll();
						if ((segChunk[arrPlace] >= 15)) {
							logger.trace("outlier #" + arrPlace + ":  " + segChunk[arrPlace]);
						}
					}
				}

				changeCalOut = seg2Differnece((objSegment).getSegmentArr(), new int[4][255]);

				diffSummed = genRowSum(changeCalOut, diffSummed);
				SegStats.addCollum(diffSummed);
				Utils.print1DArr(diffSummed, logger);

				// put the handing over code here

			} else {
				try {
					Thread.currentThread().sleep(25);
					// try territory
				} catch (InterruptedException e) {

					e.printStackTrace();
					// catch territory
				}
			}

		}

	}

	public boolean addData(Segment s) {

		if (segQueue.size() <= maxQueueSize) {
			segQueue.add(s);

			return true;
		}
		return false;

	}

	int[][] seg2Differnece(int[][] segArr, int[][] output) {

		logSegArray(segArr, "Segment array in Consumer 1");

		StringBuffer objStringBuffer = new StringBuffer();
		objStringBuffer.append(" segment2 Difference : \n");

		for (int rows = 0; rows < 4; rows++) {
			for (int columns = 0; columns < 255; columns++) {
				int a = segArr[rows][(columns + 1)];
				int b = segArr[rows][columns];

				int x = a - b;
				x = Math.abs(x);
				output[rows][columns] = x;
				objStringBuffer.append("[" + a + " - " + b + "=" + x + "],");
				// System.out.println("Consumer1 : Method seg2Differnece : assigned: Row: " +
				// rows + " Cols " + columns+ "Value : " + output[rows][columns]);
			}
			objStringBuffer.append("\n");

		}
		logger.trace(objStringBuffer.toString());

		return output;
	}

	int[] genRowSum(int[][] inputArr, int[] diffSummed) {
		for (int i = 0; i < inputArr.length - 1; ++i)
			diffSummed[i] = 0;

		for (int columns = 0; columns < 255; columns++) {
			for (int rows = 0; rows < 4; rows++) {
				diffSummed[rows] = inputArr[rows][columns] + diffSummed[rows];
			}

		}

		StringBuffer objStringBuffer = new StringBuffer();
		objStringBuffer.append(" Differrence Summed : \n");
		for (int i = 0; i < inputArr.length - 1; ++i) {
			objStringBuffer.append(" " + diffSummed[i]);
		}
		logger.info(objStringBuffer.toString());

// method body here
		return diffSummed;

	}

	// class body here again
	static int[] IQR(int[] a, int n) {
		Arrays.sort(a);

		// Index of median
		// of entire data
		int mid_index = median(a, 0, n);
		// System.out.println("Median index is " + mid_index + " Element is " +
		// a[mid_index]);

		// Median of first half
		int q1_Index = median(a, 0, mid_index);
		int Q1 = a[q1_Index];
		// System.out.println("Median index is " + q1_Index + " Element is " +
		// a[q1_Index]);

		int q3_Index = median(a, mid_index + 1, n);
		// System.out.println("Median index is " + q3_Index + " Element is " +
		// a[q3_Index]);
		// Median of second half
		int Q3 = a[median(a, mid_index + 1, n)];

		// IQR calculation
		int[] x = { (Q3 - Q1), q1_Index, q3_Index };
		return x;

	}

	static int median(int a[], int l, int r) {
		int n = r - l + 1;
		n = (n + 1) / 2 - 1;
		return n + l;
	}

	static LinkedBlockingQueue<Integer> findOutliers(int iqr, int[] segChunk, int q1, int q3) {
		LinkedBlockingQueue<Integer> elements = new LinkedBlockingQueue<Integer>();
		for (int i = 0; i < segChunk.length; i++) {
			int x = segChunk[i];
			if (x > ((1.5 * iqr) + q3) || x < (q1 - (1.5 * iqr))) {
				elements.add(i);
			}
		}
		return elements;

	}

	void logSegArray(int[][] segArr, String prefix) {
		StringBuffer obStringBuffer1 = new StringBuffer();
		obStringBuffer1.append(prefix + ":\n");
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 256; col++) {
				obStringBuffer1.append(" " + segArr[row][col]);

			}
			obStringBuffer1.append("\n");
		}
		logger.info(obStringBuffer1.toString());
	}
}
