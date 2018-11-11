package com.agent.rit;

import java.lang.instrument.*;
import java.security.ProtectionDomain;
import javassist.*;


public class CClassLoader implements ClassFileTransformer {

	private Integer timeout = 10;

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	@Override
	public byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain,
			byte[] bytes) throws IllegalClassFormatException {
		if ("com/ghc/ghTester/testexecution/ScheduleExecutionTerminator".equals(s)) {
			LoggingHelper.getInstance().info("Entered into ScheduleExecutionTerminator package");
			try {
				


				ClassPool clsPool = ClassPool.getDefault();
				LoggingHelper.getInstance().info("GOt default ClassPool");

				clsPool.appendClassPath(new LoaderClassPath(classLoader));
				LoggingHelper.getInstance().info("Appened default class path");

				CtClass cl = clsPool.getOrNull("com.ghc.ghTester.testexecution.ScheduleExecutionTerminator");
				LoggingHelper.getInstance().info("Got ScheduleExecutionTerminator class");

				CtConstructor constructor = cl.getDeclaredConstructors()[0];
				LoggingHelper.getInstance().info("Got constructor for ScheduleExecutionTerminator class");

				CtField[] fields = null;
				try {
					fields = cl.getDeclaredFields();
					if (fields != null && fields.length > 0) {

						boolean timeoutNotFound = true;

						for (CtField ctField : fields) {

							switch (ctField.getName()) {
							case "delay":
								timeoutNotFound = false;
								LoggingHelper.getInstance().info("Found timeout field named as 'delay'. Patched with following value: "
										+ this.timeout.toString());
								constructor.insertAfter("delay=" + this.timeout.toString() + "; this.delay = "
										+ this.timeout.toString() + ";");
								break;
							case "delayInMins":
								timeoutNotFound = false;
								LoggingHelper.getInstance().info("Found timeout field named as 'delayInMins'. Patched.");
								constructor.insertAfter("this.delayInMins = " + this.timeout.toString() + ";");
								break;
							}
						}

						if (timeoutNotFound)
							LoggingHelper.getInstance().info(
									"Cannot find timeout field. Maybe you use another version of Rational Integration Tester");
					}
				} catch (Exception ex) {
					LoggingHelper.getInstance().warning(ex.getMessage());
				}

				byte[] byteCode = cl.toBytecode();
				cl.detach();
				LoggingHelper.getInstance().info("Leave ScheduleExecutionTerminator package");

				return byteCode;
			} catch (Exception ex) {
				ex.printStackTrace();
				LoggingHelper.getInstance().error(ex.getMessage());
			}
		}

		return null;
	}
}
