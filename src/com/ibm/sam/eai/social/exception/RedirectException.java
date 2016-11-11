package com.ibm.sam.eai.social.exception;

import com.ibm.sam.eai.social.resources.ErrorResponseMessageFormat;

public class RedirectException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object[] messageVariables = null;
	private String redirectUrl;
    	
	/**
	 * Creates an exception identified by the given message.
	 * 
	 * @param message an error message.
	 */
	public RedirectException (String redirectUrl) {
		this.redirectUrl=redirectUrl;
	}
    
    /**
     * Gets the message key identifying this exception.
     * @return the message key identifying this exception.
     */
    public String getRedirectUrl() {
    	return this.redirectUrl;
    }
   
    /**
     * Constructs a localized message for this exception for the specified
     * locale.
     * 
     * @param locale the current locale.
     * @return localized message for this exception.
     */
	public String getMessage() {
		String key = super.getMessage();
		Throwable cause = super.getCause();
		StringBuffer stringBuf = new StringBuffer();
		
		// Create a message of localized text if a message key
		// is given
		if (key != null) {
			if (messageVariables != null) {
				stringBuf.append(ErrorResponseMessageFormat.format(key, messageVariables));
			} else {
				stringBuf.append(key);
			}
		}
 		return stringBuf.toString();
	}
}
