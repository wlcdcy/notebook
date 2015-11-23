package com.example.license;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

public class KeyPairUtil {

	public static final String KEY_ALGORITHM = "RSA";
	public static KeyPair generatorKeyPair(String seed) throws Exception {
		KeyPairGenerator kpGenerator = KeyPairGenerator
				.getInstance(KEY_ALGORITHM);
		SecureRandom random = new SecureRandom();
		random.setSeed(seed.getBytes());
		kpGenerator.initialize(2014, random);
		KeyPair keyPair = kpGenerator.generateKeyPair();
		return keyPair;
	}
	
	
}
