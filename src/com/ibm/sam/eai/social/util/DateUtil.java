package com.ibm.sam.eai.social.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.ibm.sam.eai.social.servlets.SocialLoginServlet;

public class DateUtil {
	
	private static final String className = DateUtil.class.getName();
	
	public static String getCurrentTimeZuluGMT(){
		
		String method ="getCurrentTimeZuluGMT";
		EAILogger.debug(className, className + ", " + method);
        
		String newDate = "";

        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));       
        newDate = formatter.format(currentDate.getTime());        
        newDate = newDate +"Z";
        
        EAILogger.debug(className, className + ", " + method + ", CurrentTimeZuluGMT = " + newDate);
        
		return newDate;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("getCurrentTimeZuluGMT: " + getCurrentTimeZuluGMT());
	}

}
