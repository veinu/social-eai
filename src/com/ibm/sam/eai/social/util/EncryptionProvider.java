package com.ibm.sam.eai.social.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import com.ibm.xml.crypto.util.Base64;

public class EncryptionProvider {
	private static Cipher cipher = null;
	private static String default_key  ="Ve ryLong Encry ptedKey!@4";
	private static String algorithm = "AES";
	private static  Key symKey = null;
	
	private static final String className = EncryptionProvider.class.getName();
	
	static {
		try {
			cipher = Cipher.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}
		try {
			symKey = KeyGenerator.getInstance(algorithm).generateKey();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
		}
		
	}
	

	public static String encrypt(String input) {
		
		String method ="encrypt";
		EAILogger.debug(className, className + ", " + method);
		
		byte[] encryptionBytes;
		try {
			encryptionBytes = encryptF(input,symKey,cipher);
			//System.out.println("Encrypted: " + new String(encryptionBytes));
			String encodedString = Base64.encode(encryptionBytes);
			return encodedString;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
		}
		return input;


	}
	public static String decrypt(String input) {
		
		String method ="decrypt";
		EAILogger.debug(className, className + ", " + method);
		
		byte[] valueDecoded= Base64.decode(input);
		//System.out.println("Decoded value is " + new String(valueDecoded));
		String decodedString = null;
		try {
			decodedString = decryptF(valueDecoded,symKey,cipher);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			EAILogger.debug(className, className + ", EXCEPTION = " + Utils.getStackTraceString(e));
			EAILogger.error(className, className + ", " + method + ", " + e.getMessage(),e);
		}
		//System.out.println("Decrypted: " +decodedString );
		return decodedString;
	}

	public static void main(String sr[]) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException{
		String str = "{\"provider\":\"amazon\",\"name\":\"opidfhu%20asdfaramdfasonde\",\"email\":\"bpladhu@gmail.com\",\"user_id\":\"amzn1.account.AGGC4KPUSGZ3ALKO3ESCILVG6PWQ\"}";
		System.out.println(encrypt(str));
		System.out.println(decrypt(encrypt(str)));
	}

	private static String decryptF(byte[] input, Key pkey, Cipher c)   throws InvalidKeyException,
		BadPaddingException, IllegalBlockSizeException {
		String method ="decryptF";
		EAILogger.debug(className, className + ", " + method);
		c.init(Cipher.DECRYPT_MODE, pkey);
		byte[] decrypt = c.doFinal(input);
		String decrypted = new String(decrypt);
		return decrypted;

	}

	private static byte[] encryptF(String input, Key symKey, Cipher c) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException{		
		String method ="decryptF";	
		EAILogger.debug(className, className + ", " + method);
		c.init(Cipher.ENCRYPT_MODE, symKey);		
		byte[] inputBytes = input.getBytes();
		return c.doFinal(inputBytes);
	}
}
