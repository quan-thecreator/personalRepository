package com.test;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import com.test.*;

public class Consumer2 extends Thread {
	static Logger logger = Logger.getLogger(Consumer2.class);
	static int processedWindowStartIndex = 0;
	static Consumer2 objConsumer2 = null;
	private static int ratingCounter = 0;
	int windowSize = 20;

	public static Consumer2 getInstance() {
		if (objConsumer2 == null) {
			objConsumer2 = new Consumer2();
			objConsumer2.setName("Consumer-2");
		}
		return objConsumer2;
	}

	@Override
	public void run() {
		// int count = 0;
		while (true) {	
		
			try {

				if (SegStats.count < windowSize) {
					Thread.currentThread().sleep(50);
				} else {
					
					if(processedWindowStartIndex < (SegStats.count - windowSize))
					{						
						processedWindowStartIndex = SegStats.count - windowSize;
						
						logger.info("Consumer 2: Level 2 Analytics : Processing Window start Index " + processedWindowStartIndex);
					
						int[][] window = getWindow();
						
						int rowNum=0;
						for(int[] row:window) {
							int rowLength = row.length;
							int[] returns = IQR(row.clone(), rowLength);
							LinkedBlockingQueue<Integer> outliers = findOutliers(returns[0], row, returns[1], returns[2]);
							printOutliers(outliers, row, rowNum);
							rowNum++;
						}
					}else
					{
						Thread.currentThread().sleep(100);
					}
				}	

				// if statement body here

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	static int[] IQR(int[] a, int n) {
		Arrays.sort(a);

		// Index of median
		// of entire data
		int mid_index = median(a, 0, n);
		logger.trace("Median index is " + mid_index + " Element is " + a[mid_index]);

		// Median of first half
		int q1_Index = median(a, 0, mid_index);
		int Q1 = a[q1_Index];
		logger.trace("Median index is " + q1_Index + " Element is " + a[q1_Index]);

		int q3_Index = median(a, mid_index + 1, n);
		logger.trace("Median index is " + q3_Index + " Element is " + a[q3_Index]);
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

	static LinkedBlockingQueue<Integer> findOutliers(int iqr, int[] arr, int q1, int q3) {
		LinkedBlockingQueue<Integer> elements = new LinkedBlockingQueue<Integer>();
		for (int i = 0; i < arr.length; i++) {
			int x = arr[i];
			if (x > ((1.5 * iqr) + q3) || x < (q1 - (1.5 * iqr))) {
				elements.add(i);
			}
		}
		return elements;

	}

	int[][] getWindow() {
		int windowStartIndex = SegStats.count - windowSize;
		int[][] window = new int[4][windowSize];
		StringBuffer objStringBuffer = new StringBuffer();
		objStringBuffer.append(" Consumer 2 array \n");
		int windowColIndex = 0;
		for (int row = 0; row < 4; row++) {
			windowColIndex = 0;
			for (int col = windowStartIndex; col < (windowStartIndex + windowSize); col++) {
				window[row][windowColIndex] = SegStats.arrDiffCollage[row][col];
				objStringBuffer.append(window[row][windowColIndex]);
				windowColIndex++;

			}
			objStringBuffer.append("/n");
		}
		logger.info(objStringBuffer.toString());
		
		
		return window;
	}
	void printOutliers(LinkedBlockingQueue<Integer> outliers, int[] row, int rowNum) {
		StringBuffer objStringBuffer = new StringBuffer();
		objStringBuffer.append("Row Num " + rowNum + " ::");
		boolean outLiersPresent = false;
			while (!(outliers.isEmpty())) {
			int outLierIndex = outliers.poll();
			
			if (row[outLierIndex] != 0) {
				objStringBuffer.append(row[outLierIndex] + ", ");				
				outLiersPresent = true;
			}
		}
		if (outLiersPresent) {
			logger.info("Level 2 Outliers are: " + objStringBuffer);
		}
	}

}
