package com.agent.rit;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.util.*;

import javax.swing.JOptionPane;

public class Extender {

	private static String workingDirectory = System.getProperty("user.dir");
	
	public static void premain(String args, Instrumentation inst) {
		CClassLoader classLoader = new CClassLoader();
		if (args != null) {
			try {
				Integer timeout = Integer.parseInt(args);
				classLoader.setTimeout(timeout);
			} catch (Exception e) {
			}
		}
		inst.addTransformer(classLoader);
	}

	private static void updateRITConfig(String timeout, boolean removeAgent) throws FileNotFoundException, IOException {
		String iniPath = workingDirectory + "\\IntegrationTester.ini";

		List<String> config = new ArrayList<String>();

		try (BufferedReader reader = new BufferedReader(new FileReader(iniPath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				config.add(line);
			}
		}

		if (removeAgent) {
			for (int i = config.size()-1; i >= 0; i--) {
				if (config.get(i).contains("-javaagent")){
					config.remove(i);
					break;
				}
			}
		}
		
		if (timeout != null && !timeout.isEmpty()) {
			int indexOfVMArgs = config.indexOf("-vmargs");
			config.add(indexOfVMArgs + 1, "-javaagent:./RITAgent.jar=" + timeout);
		}
		
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(iniPath))){
			for (String parameter : config) {
				writer.write(parameter);
				writer.newLine();
			}
			writer.flush();
		}
	}

	public static void main(String[] args) {
		try {
			int input = JOptionPane.showConfirmDialog(null,
					"Do you want to configure your tiemout for RIT ? If you click NO, will be used default value.",
					"Select an Option...", JOptionPane.YES_NO_CANCEL_OPTION);
			if (input == 1) { // Selected NO
				updateRITConfig(null, true);
			} else if (input == 0) {

				String timeout = JOptionPane.showInputDialog("Enter timeout value:", "10");
				updateRITConfig(timeout, true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
