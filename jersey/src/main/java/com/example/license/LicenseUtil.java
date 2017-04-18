package com.example.license;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.exception.JsonObj2StrException;
import com.example.exception.JsonStr2ObjException;

//数据:每次颁发License时提供【可以格式化如：[有效期-用户数-空间限制-服务等]】。对称密钥先对数据加密
//对称秘钥:每次颁发License时提供,对数据进行加密。
//非对称密钥对:固定的，私钥保留，公钥随程序一起。
//（注：对称秘钥与数据是一一对应的一对。）
//
//
//License颁发
//1、输入对称秘钥。
//2、用私钥对 【对称秘钥】+【数据】进行加密，结果作为license。
//
//License验证
//3、用公钥对【license】进行解密。成功license有效，保存license。
//4、解密结果【数据】+【硬件信息】用对称秘钥加密保存，启动时解密验证初始化系统。

/**
 * @author Administrator
 *
 */
public class LicenseUtil {
    public static Logger log = LoggerFactory.getLogger(LicenseUtil.class);
    private static ObjectMapper mapper = new ObjectMapper();

    public static String generateSecret(String key, LicenseData data) {
        return key + DateSerializer.dateFormat(data.getEndTime());
    }

    public static <T> T convertToObjcet(String license, Class<T> clazz) throws JsonStr2ObjException {
        try {
            return mapper.readValue(license, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            throw new JsonStr2ObjException(e);
        }
    }

    public static String convertToString(Object data) throws JsonObj2StrException {
        try {
            return mapper.writeValueAsString(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new JsonObj2StrException(e);
        }
    }

    public static String generateLicense(String secret, LicenseData data, PrivateKey pri_key) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("data", DESUtil.encrypt(convertToString(data), secret));
        map.put("secret", secret);
        try {
            String source = convertToString(map);
            String license = RSAUtil.encrypt(source, pri_key);
            return license;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static LicenseData parseLicense(String secret, String license, PublicKey pub_key) {
        try {
            Map<String, String> _obj = convertToObjcet(RSAUtil.decrypt(license, pub_key), Map.class);
            LicenseData data = (LicenseData) convertToObjcet(DESUtil.decrypt(_obj.get("data"), _obj.get("secret")),
                    LicenseData.class);
            if (StringUtils.equals(generateSecret(secret, data), _obj.get("secret"))) {
                return data;
            }
        } catch (JsonStr2ObjException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static LicenseData parseLicense(String secret, String license) {
        try {
            PublicKey pub_key = SerializableUtil.DeserializablePublicKey("");
            Map<String, String> _obj = convertToObjcet(RSAUtil.decrypt(license, pub_key), Map.class);
            LicenseData data = (LicenseData) convertToObjcet(DESUtil.decrypt(_obj.get("data"), _obj.get("secret")),
                    LicenseData.class);
            if (StringUtils.equals(generateSecret(secret, data), _obj.get("secret"))) {
                return data;
            }
        } catch (JsonStr2ObjException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public static void main(String[] args) {
        String key = "A1B2C3D4E5F6";
        LicenseData licenseData = new LicenseData();
        licenseData.setUserNum(50000000);
        licenseData.setSpaceSum(1000000000);
        licenseData.setEndTime(new Date());
        try {
            String source = convertToString(licenseData);
            log.info("license data ： " + source);
            String secret = generateSecret(key,licenseData);
            log.info("对称密钥 ： " + secret);
            String encryptData = DESUtil.encrypt(source, secret);
            log.info("对称密钥加密license data后: " + encryptData);
            Map<String, String> ml = new HashMap<String, String>();
            ml.put("data", encryptData);
            ml.put("secret", secret);
            String license = convertToString(ml);
            log.info("license : " + license);

            String seed = "1";
            String encrypt_license = RSAUtil.encrypt(license, seed);
            log.info("encrypt_license : " + encrypt_license);
            String decrypt_license = RSAUtil.decrypt(encrypt_license, seed);
            log.info("decrypt_license : " + decrypt_license);

            @SuppressWarnings("unchecked")
            Map<String, String> decrypt_eicense_obj = convertToObjcet(decrypt_license, Map.class);
            String encrypt_data = decrypt_eicense_obj.get("data");
            log.info("encrypt_data : " + encrypt_data);
            String secret_ = decrypt_eicense_obj.get("secret");
            // LicenseData ld
            // =generateLicense(decrypt_license,LicenseData.class);
            // secret_+=ld.getEndTime().getTime();
            log.info("对称密钥—— : " + secret_);
            String decryptData = DESUtil.decrypt(encrypt_data, secret_);
            System.out.println("解密后: " + decryptData);

            Sigar sigar = new Sigar();
            try {
                Cpu cpu = sigar.getCpu();
                log.info(cpu.toString());
            } catch (SigarException e) {
                e.printStackTrace();
            }

            OperatingSystem os = OperatingSystem.getInstance();

            log.info(os.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
