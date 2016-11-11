package com.ibm.sam.eai.social.exception;



import com.ibm.sam.eai.social.resources.ErrorResponseMessageFormat;

public class SocialEAIException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object[] messageVariables = null;
	private String code = null;
    	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * Creates an exception identified by the given message.
	 * 
	 * @param message an error message.
	 */
	public SocialEAIException (String message) {
		super(message);
	}
	
	/**
	 * Creates an exception identified by the given message.
	 * 
	 * @param message an error message.
	 * @param code an error code.
	 */
	public SocialEAIException (String code, String message) {
		super(message);
		this.code = code;
	}
	
	/**
	 * Creates an exception identified by the given message.
	 * 
	 * @param message an error message.
	 * @param msgType constant from MessageInfo for error/warning/info
	 */
	public SocialEAIException (String message,int msgType) {
		super(message);
	}

    /**
     * Creates an exception identified by the given message key.
     * @param messageKey key for an error message.
     * @param messageVars message variables.
     */
    public SocialEAIException (String messageKey, Object[] messageVars)
    {
        super(messageKey);
        this.messageVariables = messageVars;
    }
       
    /**
     * Creates an exception encapsulating another throwable.
     * 
     * @param cause root cause of this exception.
     */   
    public SocialEAIException(Throwable cause) {
    	super(cause);
    }
    
    /**
     * Creates an exception identified by the given message and
     * encapsulating another throwable.
     * 
     * @param message an error message.
     * @param cause root cause of this exception.
     */
	public SocialEAIException(String message, Throwable cause) {
		super(message, cause);
	}
    
	public SocialEAIException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}
	
    /**
     * Creates an exception identified by the given message key and
     * encapsulating another throwable.
     * 
     * @param messageKey key for an error message.
     * @param messageArgs arguments to the message key
     * @param cause root cause of this exception.
     */
    public SocialEAIException(String messageKey, Object[] messageArgs, Throwable cause) {
        super(messageKey, cause);
        this.messageVariables = messageArgs;
    }
    
    public SocialEAIException(String code, String messageKey, Object[] messageArgs, Throwable cause) {
        super(messageKey, cause);
        this.messageVariables = messageArgs;
        this.code = code;
    }
    
    
    /**
     * Gets the message key identifying this exception.
     * @return the message key identifying this exception.
     */
    public String getMessageKey() {
    	return super.getMessage();
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
