package com.example.license;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

public class RSAUtil2 {
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";

    /**
     * 私钥加密数据
     * 
     * @param data
     * @param pri_key
     * @return
     * @throws Exception
     */
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
     * 公钥解密数据
     * 
     * @param data
     * @param pub_key
     * @return
     * @throws Exception
     */
    public static String decrypt(String data, PublicKey pub_key) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 初始化Cipher对象，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, pub_key);
        // 执行解密操作
        return new String(cipher.doFinal(Base64.decodeBase64(data)));
    }

}
