package com.test;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
 import com.test.*;
public class TestController {	
	
	static Logger logger = Logger.getLogger(TestController.class);
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
		if(logger.isInfoEnabled()) {
		logger.info("Log4j has started working");
		}
		
	/*	Thread accepter = new Thread(DataAccepter.getInstance());
		Thread sensorDataReader = new Thread(SensorDataReader.getInstance());
		Thread consumer2 = new Thread(Consumer2.getInstance());
		Thread consumer1 = new Thread(Consumer1.getInstance());
		Thread dataProcessor = new Thread()
		
		*/
		Thread accepter =  DataAccepter.getInstance();
		Thread sensorDataReader = SensorDataReader.getInstance();
		Thread consumer2 = Consumer2.getInstance();
		Thread consumer1 = Consumer1.getInstance();
		Thread dataProcessor =  SensorDataProcessor.getInstance();
		
		accepter.start();
		consumer2.start();
		consumer1.start();
		sensorDataReader.start();
		dataProcessor.start();
	}
	
	
}
