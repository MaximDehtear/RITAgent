package com.agent.rit;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggingHelper {
	private static volatile LoggingHelper instance;
	
	private Logger log = Logger.getLogger("RITAgent");
	private FileHandler fh = null;
	private SimpleFormatter formatter = new SimpleFormatter();
	private String workingDirectory =  System.getProperty("user.dir");
	
	public static LoggingHelper getInstance() {
		LoggingHelper localInstance = instance;
		if (localInstance == null) {
			synchronized (LoggingHelper.class) {
				localInstance = instance;
				if (localInstance == null) {
					
					instance = localInstance = new LoggingHelper();
				}
			}
		}
		return localInstance;
	}
	
	private Logger getLogger() throws SecurityException, IOException {
		if (fh == null) {
			fh = new FileHandler(workingDirectory + "\\RITAgent_Log.txt");
			fh.setFormatter(formatter);
			log.addHandler(fh);
		}
		return log;
	}
	private void writeLog(String msg, int level) {
		try {
			switch (level) {
			case 0:
				getLogger().info(msg);
				break;
			case 1:
				getLogger().warning(msg);
				break;
			case 2:
				getLogger().severe(msg);
				break;
			default:
				break;
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void info(String msg) {
		writeLog(msg, 0);
	}
	public void warning(String msg) {
		writeLog(msg, 1);
	}
	public void error(String msg) {
		writeLog(msg, 2);
	}

}
