package com.ibm.sam.eai.social.resources;

public class ErrorResponseMessageFormat {

	public static String format(String pattern, Object[] messageVariables) {
	   String raw = pattern;
	   for(int i=0;i<messageVariables.length;i++){
		   System.out.println("i = " +i +" :: " +messageVariables[i]);
		   String s = "%"+i;
		   String msg = "";
		   if(messageVariables[i]==null)
			   raw = raw.replaceAll(s, "");
		   else if(messageVariables[i] instanceof String)
			   raw = raw.replaceAll(s, (String)messageVariables[i]);
		   else
			   raw = raw.replaceAll(s, messageVariables[i].toString());
	   }
	   return raw;
    }
}
