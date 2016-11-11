package com.ibm.sam.eai.social.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import com.ibm.sam.eai.social.exception.SocialEAIException;
import com.ibm.sam.eai.social.provider.SocialLoginProvider;
import com.ibm.sam.eai.social.resources.ErrorCodes;

public class PropertiesManager {
	private static Properties properties = null;
	private static PropertiesManager propertiesManager = null;

	private static final String className = PropertiesManager.class.getName();
	private static final String PROPERTIES_FILE = "SocialEAI.properties";
	private static final String CLASSPATH_PROPERTY = "java.class.path";
	/*public static final String SOCIAL_CONF_FILE = (String) CustomerProperty.getInstance().getProperty("socialConfigFile");*/
	
	private void load() throws FileNotFoundException, IOException {
		String method ="load";
		EAILogger.debug(className, className + ", " + method);
		properties = new Properties();
		
		/*
		String dir = findDataDir();
		//String dir = "D:\\WorkSpace\\SOCIAL_EAI\\Data";
		EAILogger.info(className, className + ", " + method + ", dir = " + dir);		
		properties.load(new FileInputStream(new File(dir, PROPERTIES_FILE)));
		if(dir==null){
			InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE);
			properties.load(inStream);
		}
		*/
		
		String dir = "C:\\";
		EAILogger.info(className, className + ", " + method + ", dir = " + dir);		
		properties.load(new FileInputStream(new File(dir, PROPERTIES_FILE)));
		if(dir==null){
			InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE);
			properties.load(inStream);
		}
		
		
		/*
		EAILogger.debug(className, className + ", SOCIAL_CONF_FILE = " + SOCIAL_CONF_FILE);
		//Load SocialEAI.properties location from was variable socialConfigFile.
		properties.load(new FileInputStream(SOCIAL_CONF_FILE));	
		*/	
	}
	private String findDataDir() {
		String method ="findDataDir";
		EAILogger.debug(className, className + ", " + method);
		// try the class path
		try {
			String classPath = System.getProperty(CLASSPATH_PROPERTY);
			classPath = "."+File.pathSeparator+classPath;
			System.out.println("classPath>"+classPath);
			StringTokenizer st = new StringTokenizer(classPath, File.pathSeparator);
			while (st.hasMoreTokens()) {
				String dirName = st.nextToken();
				File file = new File(dirName, PROPERTIES_FILE);
				if (file.isFile()) {
					System.out.println("dirName>"+dirName);
					return dirName;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
		}
		return null;
	}
	public static PropertiesManager getInstance() throws SocialEAIException {
		String method ="getInstance";
		EAILogger.debug(className, className + ", " + method);
		try {
			if (propertiesManager == null) {
				EAILogger.debug(className, className + ", propertiesManager is null.");
				propertiesManager = new PropertiesManager();
				propertiesManager.load();
			}else{
				EAILogger.debug(className, className + ", propertiesManager is not null.");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(SocialLoginProvider.class.getName(),e);
			String msg [] = {PROPERTIES_FILE,e.getMessage()};
			throw new SocialEAIException(ErrorCodes.SOCEAI108E,msg);
		} catch (IOException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(SocialLoginProvider.class.getName(),e);
			String msg [] = {PROPERTIES_FILE,e.getMessage()};
			throw new SocialEAIException(ErrorCodes.SOCEAI109E,msg);
		}
		return propertiesManager;
	}
	
	public String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}
	
}
