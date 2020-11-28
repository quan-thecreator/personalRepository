package com.test;

import org.apache.log4j.Logger;

public class Utils {

	public static void print1DArr(int[] arr, Logger log) {

		for (int x : arr) {
			log.info(x);
		}
	}

	public static void printArr(int[][] testArr, Logger log) {
		for (int rows = 0; rows < testArr.length; rows++) {
			for (int columns = 0; columns < testArr[0].length; columns++) {
				log.info(testArr[rows][columns] + "  ");

			}
			System.out.println("");
		}
	}
	
}
