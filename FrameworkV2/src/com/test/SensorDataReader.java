package com.test;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.fazecast.jSerialComm.SerialPort;

public class SensorDataReader extends Thread {
	public static SensorDataReader reader;
	static Logger logger = Logger.getLogger(SensorDataReader.class);
	static SerialPort ubxPort;
	public static LinkedBlockingQueue<Integer> sensorDataQueue = new LinkedBlockingQueue<Integer>();

	public static SensorDataReader getInstance() {

		if (reader == null) {
			reader = new SensorDataReader();
			reader.setName("SensorReader");
			logger.info("Changed getUbxInstance as one time calling method");

			SerialPort[] ports = SerialPort.getCommPorts();
			ubxPort = ports[0];

			boolean statusLevel = ubxPort.openPort(0);
			if (statusLevel) {
				logger.trace("port open");
			} else
				logger.fatal("port opening failed");
			ubxPort.setBaudRate(9600);
			ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1000, 0);
		}
		return reader;
	}

	public void run() {
		while (true) {
			byte[] readBuffer = new byte[4096];
			int numRead = ubxPort.readBytes(readBuffer, readBuffer.length);
			if(numRead > 0) {
				for (int i = 0; i < numRead; i++) {
					int readValue = (int) (readBuffer[i]);
					int mod = i%4;
					
					if(mod < 2) {
						sensorDataQueue.add(readValue);
					}
					logger.trace("Bytes Read "+ numRead + "Index: "+ i+ "  : Returning val from get data method: " + readValue);
				}
			}else {
				try {
					logger.trace("Received bytes " + numRead + "Sleeping for 25 millis");
					Thread.currentThread().sleep(25);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		

	}
}

//logger.trace("1 -> Read " + numRead + " bytes." + " Data : " + new
		// String(readBuffer));

		/*
		 * if (numRead > 0) {
		 * 
		 * int availableInteggersInBuffer = (int)numRead/4;
		 * 
		 * 
		 * byte[] bufferArr = new byte[4]; for (int i = 0; i <
		 * availableInteggersInBuffer; i++) { int offset = i % 4; bufferArr[offset] =
		 * readBuffer[i]; if (offset == 3) { ByteBuffer wrapped =
		 * ByteBuffer.wrap(bufferArr); // big-endian by default int readValue =
		 * wrapped.getInt(); // 1 if (readValue > 0) { sensorDataQueue.add(readValue);
		 * logger.trace("Returning val from get data method: " + readValue); } bufferArr
		 * = new byte[4]; } }
		 * 
		 * }
		 */