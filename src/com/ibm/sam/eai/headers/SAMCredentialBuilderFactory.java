package com.ibm.sam.eai.headers;

import com.ibm.sam.eai.headers.impl.SAMCredentialBuilderImpl;

public class SAMCredentialBuilderFactory {

	static SAMCredentialBuilderFactory instance = null;
	static String classname = SAMCredentialBuilderFactory.class.getName();
	private SAMCredentialBuilderFactory(){
		
	}
	public static SAMCredentialBuilderFactory getInstance(){
		if(instance==null){
			synchronized(classname){
				instance = new SAMCredentialBuilderFactory();
			}
		}
		return instance;
	}
	public SAMCredentialBuilder getSAMCredentialBuilder(){
		SAMCredentialBuilder provider = new SAMCredentialBuilderImpl();
		return provider;
	}	
}
