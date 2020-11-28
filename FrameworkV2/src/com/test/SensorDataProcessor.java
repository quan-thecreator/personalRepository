package com.test;

import org.apache.log4j.Logger;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Scanner;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import com.test.*;

public class SensorDataProcessor extends Thread {

	private static int max = 79;
	private static int min = 50;
	static SensorDataProcessor objSim = null;
	static int spikeCounter = 0;
	static Logger logger = Logger.getLogger(SensorDataProcessor.class);
	static SerialPort ubxPort;

	public static SensorDataProcessor getInstance() {

		if (objSim == null) {
			objSim = new SensorDataProcessor();
			objSim.setName("SensorDataProcessor");

		}
		return objSim;
	}

	@Override
	public void run() {
		int count = 0;
		try {

			DataAccepter objDataAccepter = DataAccepter.getInstance();
			while (true) {

				if (SensorDataReader.sensorDataQueue != null) {

					Integer SensorReading = (SensorDataReader.sensorDataQueue).poll();
					if (SensorReading != null) {

						logger.trace("Data recieved: " + SensorReading);
						long startMills = System.currentTimeMillis();

						boolean conformation = objDataAccepter.addData(SensorReading);

						count++;
						if (count == 1024) {
							long sleepTime = (startMills + 1000) - System.currentTimeMillis();

							if (!(sleepTime < 0)) {
								Thread.currentThread().sleep(sleepTime);
								logger.info("Wrote 1024 values. Sleeping for " + sleepTime + " Millis");
							}

							count = 0;
						} // If closer for count 1024
					} // If closer for sensor reading value not null
				} // if closer for sensor data queue not null
			} // While loop ending

		} catch (Exception e) {
			logger.trace("Exiting from while loop of run method");
			e.printStackTrace();
			logger.fatal("exiting the thread");
			Thread.currentThread().stop();
		} // cath ending
	}
}
