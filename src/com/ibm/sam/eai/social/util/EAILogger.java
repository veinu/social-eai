package com.ibm.sam.eai.social.util;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.PropertyConfigurator;

import com.ibm.sam.eai.social.exception.SocialEAIException;

public class EAILogger {
	
	private static final String className = EAILogger.class.getName();
	private static Log logger; 
	
	public final static String LOG4_PROP_FILE_LOC =  "LOG4_PROP_FILE_LOC";
	
	static {
		logger = getLogger(EAILogger.class.getName());
		
		String log4JPropertyFile = "C:\\log4j.properties";
		/*String log4JPropertyFile = "";
		try {
			log4JPropertyFile = PropertiesManager.getInstance().getProperty(LOG4_PROP_FILE_LOC);	
			EAILogger.debug(className, className + ", log4JPropertyFile = " + log4JPropertyFile);
		} catch (SocialEAIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", Error getting property " + LOG4_PROP_FILE_LOC);
			EAILogger.error(className, ", msg=" + e.getMessage() + ", code=" +e.getCode(), e);
		}*/
		EAILogger.debug(className, className + ", log4JPropertyFile = " + log4JPropertyFile);
		
		Properties p = new Properties();		
		try {
		    p.load(new FileInputStream(log4JPropertyFile));
		    PropertyConfigurator.configure(p);		   
		} catch (IOException e) {
		    e.printStackTrace();
		    EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		    EAILogger.error(className, className + ", Error loading Log4j properties file " + log4JPropertyFile);
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("Initialized logging - INFO");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Initialized logging - DEBUG");
		}
		
		System.out.println(" logger ("+logger+") initialized." + logger.toString());
		EAILogger.debug(className, className + ", logger ("+logger+") initialized." + logger.toString());
	}

	public static final Log getLogger(String classname) {
		Log4JLogger thisLog = new Log4JLogger(classname);
		return thisLog;
	}

	public static final void debug(String className, Object obj, Throwable t) {
		System.out.println(className +":"  + obj);
		//Log logger = getLogger(className);
		if (logger.isDebugEnabled())
			logger.debug(obj, t);
	}
	
	public static final void fatal(String className, Object obj, Throwable t) {
		//Log logger = getLogger(className);
		if (logger.isFatalEnabled())
			logger.fatal(obj, t);
	}
	
	public static final void error(String className, Object obj, Throwable t) {
		//Log logger = getLogger(className);
		if (logger.isFatalEnabled())
			logger.fatal(obj, t);
	}
	
	public static final void info(String className, Object obj, Throwable t) {
		//Log logger = getLogger(className);
		if (logger.isInfoEnabled())
			logger.info(obj, t);
	}
	
	public static final void trace(String className, Object obj, Throwable t) {
		//Log logger = getLogger(className);
		if (logger.isTraceEnabled())
			logger.trace(obj, t);
	}
	
	public static final void warn(String className, Object obj, Throwable t) {
		//Log logger = getLogger(className);
		if (logger.isWarnEnabled())
			logger.warn(obj, t);
	}

	public static final void debug(String className, Object obj) {
		//Log logger = getLogger(className);
		System.out.println(className+"::"+obj);
		if (logger.isDebugEnabled())
			logger.debug(obj);
	}
	
	public static final void fatal(String className, Object obj) {
		//Log logger = getLogger(className);
		if (logger.isFatalEnabled())
			logger.fatal(obj);
	}
	
	public static final void error(String className, Object obj) {
		//Log logger = getLogger(className);
		if (logger.isFatalEnabled())
			logger.fatal(obj);
	}
	
	public static final void info(String className, Object obj) {
		//Log logger = getLogger(className);
		System.out.println(className +":"  + obj);
		if (logger.isInfoEnabled())
			logger.info(obj);
	}
	
	public static final void trace(String className, Object obj) {
		//Log logger = getLogger(className);
		if (logger.isTraceEnabled())
			logger.trace(obj);
	}
	
	public static final void warn(String className, Object obj) {
		//Log logger = getLogger(className);
		if (logger.isWarnEnabled())
			logger.warn(obj);
	}	

	public static void main(String[] args){
		logger.info("Test info");
		logger.debug("Test info");
	}
}
