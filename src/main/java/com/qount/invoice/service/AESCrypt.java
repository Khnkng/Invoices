package com.qount.invoice.service;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class AESCrypt {
	
	private static final Logger LOGGER  = Logger.getLogger(AESCrypt.class);
	
	private static final String ALGORITHM = "AES";

	public static String encrypt(String value, String secret) throws Exception {
		Key key = prepareKey(secret);
		Cipher cipher = Cipher.getInstance(AESCrypt.ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
		String encryptedValue64 = Base64.encodeBase64String(encryptedByteValue);
		LOGGER.debug("encrypt value " + encryptedValue64);
		return encryptedValue64;

	}

	public static String decrypt(String value, String secret) throws Exception {
		String decryptedValue = null;
		try {
			Key key = prepareKey(secret);
			Cipher cipher = Cipher.getInstance(AESCrypt.ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decryptedValue64 = Base64.decodeBase64(value);
			byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
			decryptedValue = new String(decryptedByteValue, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decryptedValue;

	}


	private static Key prepareKey(String secret) {
		Key key = new SecretKeySpec(secret.getBytes(), AESCrypt.ALGORITHM);
		LOGGER.debug("Key " + key);
		return key;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(decrypt("AP5djTMcjqXG2JFI8rP+P0v/w9t4BZ5EsIcJ1TwQTO0=","1Hbfh667adfDEJ78"));
	}

}
