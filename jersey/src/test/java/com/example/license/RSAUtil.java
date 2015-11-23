package com.example.license;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

public class RSAUtil {
	// 算法名称
	public static final String KEY_ALGORITHM = "RSA";
	public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";

	public static KeyPair generatorKeyPair(String seed) throws Exception {
		KeyPairGenerator kpGenerator = KeyPairGenerator
				.getInstance(KEY_ALGORITHM);
		SecureRandom random = new SecureRandom();
		random.setSeed(seed.getBytes());
		kpGenerator.initialize(2014, random);
		KeyPair keyPair = kpGenerator.generateKeyPair();
		return keyPair;
	}

	/**
	 * 加密数据
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @return 加密后的数据
	 */
	public static String encrypt(String data, String seed) throws Exception {
		KeyPair keyPair = generatorKeyPair(seed);
		// 实例化Cipher对象，它用于完成实际的加密操作
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// SecureRandom random = new SecureRandom();
		// 初始化Cipher对象，设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
		byte[] results = cipher.doFinal(data.getBytes());
		// 该部分是为了与加解密在线测试网站（http://tripledes.online-domain-tools.com/）的十六进制结果进行核对
		for (int i = 0; i < results.length; i++) {
			System.out.print(results[i] + " ");
		}
		System.out.println();
		// 执行加密操作。加密后的结果通常都会用Base64编码进行传输
		return Base64.encodeBase64String(results);
	}
	
	public static String encrypt(String data, PrivateKey pri_key) throws Exception {
		// 实例化Cipher对象，它用于完成实际的加密操作
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// SecureRandom random = new SecureRandom();
		// 初始化Cipher对象，设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, pri_key);
		byte[] results = cipher.doFinal(data.getBytes());
		// 该部分是为了与加解密在线测试网站（http://tripledes.online-domain-tools.com/）的十六进制结果进行核对
		for (int i = 0; i < results.length; i++) {
			System.out.print(results[i] + " ");
		}
		System.out.println();
		// 执行加密操作。加密后的结果通常都会用Base64编码进行传输
		return Base64.encodeBase64String(results);
	}

	/**
	 * 解密数据
	 * 
	 * @param data
	 *            待解密数据
	 * @param key
	 *            密钥
	 * @return 解密后的数据
	 */
	public static String decrypt(String data, String seed) throws Exception {
		KeyPair keyPair = generatorKeyPair(seed);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 初始化Cipher对象，设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, keyPair.getPublic());
		// 执行解密操作
		return new String(cipher.doFinal(Base64.decodeBase64(data)));
	}

	public static String decrypt(String data, PublicKey pub_key)
			throws Exception {
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 初始化Cipher对象，设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, pub_key);
		// 执行解密操作
		return new String(cipher.doFinal(Base64.decodeBase64(data)));
	}

	public static void main(String[] args) throws Exception {
		String source = "amigoxie";
		System.out.println("原文: " + source);
		String seed = "1";
		String encryptData = encrypt(source, seed);
		System.out.println("加密后: " + encryptData);
		String decryptData = decrypt(encryptData, seed);
		System.out.println("解密后: " + decryptData);
	}
}
