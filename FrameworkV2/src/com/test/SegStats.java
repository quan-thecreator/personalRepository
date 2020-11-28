package com.test;

public class SegStats {

	static short[][] arrDiffCollage = new short[4][100000];
	static int count = 0;

	public static boolean addCollum(int[] arr) {
		try {
			if (count != 100000) {
				for (short rows = 0; rows < 4; rows++) {
					arrDiffCollage[rows][count] = (short) arr[rows];
				}
				++count;

			} 			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
