package com.ibm.sam.eai.ldap.impl;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class LdapConnection {

    private LdapContext ctx = null;

    // default constructor
    public LdapConnection() {}

	public LdapConnection(LdapContext ctx) {
		this.ctx = ctx;
	}

	public LdapConnection(Map env)
		throws CommunicationException, NamingException {
		this.ctx = new InitialLdapContext((Hashtable)env, new Control[]{});
	}

	public LdapContext getConnection() {
		return ctx;
	}

	public void setConnection(LdapContext ctx) {
    	this.ctx = ctx;
    }
}
