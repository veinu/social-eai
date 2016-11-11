package com.customer.user.data.provider;

import com.customer.user.data.provider.impl.SAMRegistryDataProviderImpl;
import com.ibm.sam.eai.social.exception.SocialEAIException;


public class CustomerDataProviderFactory {

	static CustomerDataProviderFactory m_instance = null;
	static CustomerDataProvider m_customerDataProvider = null;
	static String classname = CustomerDataProviderFactory.class.getName();
	private CustomerDataProviderFactory(){
		
	}
	public static CustomerDataProviderFactory getInstance() throws SocialEAIException{
		if(m_instance==null){
			synchronized(classname){
				m_instance = new CustomerDataProviderFactory();
				//m_customerDataProvider = new CustomerDataProviderImpl();
				m_customerDataProvider = new SAMRegistryDataProviderImpl();
				
			}
		}
		return m_instance;
	}
	public CustomerDataProvider getCustomerDataProvider(){
		return m_customerDataProvider;
	}
}
