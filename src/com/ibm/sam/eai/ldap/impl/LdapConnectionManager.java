package com.ibm.sam.eai.ldap.impl;

import java.util.Hashtable;
import java.util.ArrayList;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;

import com.ibm.sam.eai.social.exception.SocialEAIException;
import com.ibm.sam.eai.social.provider.SocialLoginProvider;
import com.ibm.sam.eai.social.resources.ErrorCodes;
import com.ibm.sam.eai.social.util.EAILogger;
import com.ibm.sam.eai.social.util.PropertiesManager;
import com.ibm.sam.eai.social.util.Utils;



public class LdapConnectionManager {
	
	private static final String className = LdapConnectionManager.class.getName();

	static LdapConnectionManager instance = null;
	static String classname = LdapConnectionManager.class.getName();
	private Hashtable m_ldap_env;
	private ArrayList<String> m_returnAttributes;
	private LdapConnectionManager(){
		m_ldap_env = new Hashtable();
		m_returnAttributes = new ArrayList<String>();
		m_ldap_env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		m_ldap_env.put(Context.SECURITY_AUTHENTICATION, "simple");
		
		PropertiesManager propertiesManager = null;
		try {
			propertiesManager = PropertiesManager.getInstance();
		} catch (SocialEAIException e) {
			EAILogger.error(SocialLoginProvider.class.getName(), e);
		}

				
		String provider_url = propertiesManager.getProperty("LDAP.PROVIDER_URL");
		String sec_principal = propertiesManager.getProperty("LDAP.SECURITY_PRINCIPAL");
		String sec_creds = propertiesManager.getProperty("LDAP.SECURITY_CREDENTIALS");
		String returnAttrList = propertiesManager.getProperty("LDAP.RETURN_ATTRIBUTES");
		
		EAILogger.debug(className, className + ", provider_url = " + provider_url);		
		EAILogger.debug(className, className + ", sec_principal = " + sec_principal);		
		EAILogger.debug(className, className + ", returnAttrList = " + returnAttrList);

		
		if(provider_url!=null)
			m_ldap_env.put(Context.PROVIDER_URL,provider_url);
		else
			m_ldap_env.put(Context.PROVIDER_URL, "ldap://localhost:389");
	
		if(sec_principal!=null)
			m_ldap_env.put(Context.SECURITY_PRINCIPAL,sec_principal);
		else
			m_ldap_env.put(Context.SECURITY_PRINCIPAL, "cn=root");
		
		if(sec_creds!=null)
			m_ldap_env.put(Context.SECURITY_CREDENTIALS,sec_creds);
		else
			m_ldap_env.put(Context.SECURITY_CREDENTIALS, "Root@1234");
		
		if(returnAttrList!=null){
			String[] tmp = returnAttrList.split(",");
			for(int i=0;i<tmp.length;i++){
				m_returnAttributes.add(tmp[i]);
			}
		}
	}
	
    public static LdapConnectionManager getInstance() throws SocialEAIException
    {
        if(instance == null)
        {
            synchronized(classname)
            {
                if(instance==null)
                    instance=new LdapConnectionManager();                
            }
        }
        return instance;
    }
    
    public ArrayList<String> getReturnAttributes(){
    	return this.m_returnAttributes;
    }
    
    public LdapConnection getConnection() throws SocialEAIException {
    	String method ="getConnection";
    	EAILogger.debug(className, className + ", " + method);
    	try {
			return new LdapConnection(m_ldap_env);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));		
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
			String msg [] = {e.getMessage()};
			throw new SocialEAIException(ErrorCodes.SOCEAI110E,msg);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));		
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
			String msg [] = {e.getMessage()};
			throw new SocialEAIException(ErrorCodes.SOCEAI110E,msg);
		}
    }
    
    /*
    public static void main(String str[]){
		com.ibm.sam.eai.ldap.impl.LdapDaoImpl ldapDao;
		try {
			ldapDao = new com.ibm.sam.eai.ldap.impl.LdapDaoImpl(com.ibm.sam.eai.ldap.impl.LdapConnectionManager.getInstance().getConnection());
			java.util.HashMap<String, ArrayList<String>> ldapObj = ldapDao.getLdapObject("bsiddhu@gmail.com");
			java.util.Set<String> keys = ldapObj.keySet();
			for(String key:keys){
				System.out.println("<tr><td>"+key +"</td><td>"+ldapObj.get(key)+"</td></tr>");
			}
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocialEAIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    */
    
}
