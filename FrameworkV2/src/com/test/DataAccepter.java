package com.test;

import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.test.*;
public class DataAccepter extends Thread {

	@SuppressWarnings("unchecked")
	LinkedBlockingQueue<Integer> sensorDataQ = new LinkedBlockingQueue<Integer>();
	static Logger logger = Logger.getLogger(DataAccepter.class);
	static final int maxQueueSize = 100000;
	static DataAccepter objDataAccepter = null;

	public static DataAccepter getInstance() {
		if (objDataAccepter == null) {
			objDataAccepter = new DataAccepter();
			objDataAccepter.setName("DataAccepter");
		}
		return objDataAccepter;
	}

	public boolean addData(int d) {

		if (sensorDataQ.size() <= maxQueueSize) {
			sensorDataQ.add(d);
			// System.out.println("DataAcceptor: Random Num added to DataAcceptorQ " + d);
			return true;
		}

		return false;
	}

	public LinkedBlockingQueue<Integer> getSensorDataQ() {
		return sensorDataQ;
	}

	public void setSensorDataQ(LinkedBlockingQueue<Integer> sensorDataQ) {
		this.sensorDataQ = sensorDataQ;
	}

	@SuppressWarnings("static-access")
	@Override
	public void run() {
		Consumer1 objCurrentConsumer = Consumer1.getInstance();
		while (true) {
			int sensorDataQueueSize = sensorDataQ.size();
			if (sensorDataQueueSize > 1024) {
				
				sample4ksegments(objCurrentConsumer);
				//

			} else {
				try {
					Thread.currentThread().sleep(25);
					// try territory
				} catch (InterruptedException e) {

					e.printStackTrace();
					// catch territory
				}
			}
			// this is while loop territory

		}

	}

	void sample4ksegments(Consumer1 objConsumer1) {

		Segment objSegment = new Segment();
		int[][] segmentArr = objSegment.getSegmentArr();
		
		
		StringBuffer objStringBuffer = new StringBuffer();
		objStringBuffer.append("Sensor Data Values are \n");

		
		int runningCounter =0;
		for (int columns = 0; columns < 256; columns++) {
			for (int rows = 0; rows < 4; rows++) {
				int dataValue = (int) sensorDataQ.poll();
				segmentArr[rows][columns] = dataValue;
				objStringBuffer = objStringBuffer.append(" " + dataValue);
				runningCounter++;
				/* System.out.println("DataAcceptor: Transferring sensor data from Q to array "
				 + segmentArr[rows][columns] ); */
				if(runningCounter % 50 == 0 ) {
					objStringBuffer.append("\n");								
				}					
			}		
			
		}
		logger.trace(objStringBuffer.toString());
		
		logSegArray(segmentArr, " Segment Array in Data Accepter");
		
		
		
		//System.out.println("DataAcceptor: Transferred 1024 values to a segment Array");
		// method body
		//objSegment.setSegmentArr(segmentArr);
		objConsumer1.addData(objSegment);

	}
	
	void logSegArray(int[][] segArr, String prefix){
		StringBuffer obStringBuffer1 = new StringBuffer();
		 obStringBuffer1.append(prefix + ":\n");			
			 for(int row=0; row<4; row++) {	
				 for(int col=0;col<256; col++) {
				 obStringBuffer1.append(" " + segArr[row][col]);				
				 
			 }
				 obStringBuffer1.append("\n");
		 }
		 logger.trace(obStringBuffer1.toString());
	}

}
