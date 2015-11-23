package com.example.license;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.codehaus.jackson.map.ObjectMapper;
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseListener implements ServletContextListener {

	public static Logger log = LoggerFactory.getLogger(LicenseListener.class);
	public static final String ENVIRONMENT_LICENSE_KEY = "key";
	public static final String ENVIRONMENT_LICENSE_VALUE = "license";
	private ServletContext sc;
	private static final String msg = "启动系统需要一个授权的license，请检查web.xml文件中的LicenseListener授权配置!";

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		sc = sce.getServletContext();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		sc = sce.getServletContext();
		String key = (String) sc.getAttribute(ENVIRONMENT_LICENSE_KEY);
		String value = (String) sc.getAttribute(ENVIRONMENT_LICENSE_VALUE);
		if (key == null) {
			throw new RuntimeException(msg);
		}
		if (value == null) {
			throw new RuntimeException(msg);
		}
		LicenseData licenseDate = LicenseUtil.parseLicense(key, value);
		if (licenseDate == null) {
			throw new RuntimeException(msg);
		}
		// TODO licenseDate初始化系统。

		// 保存 【数据+硬件信息】对称密钥加密后的。
	}

	public static void main(String[] args) {
		String key = "A1B2C3D4E5F6";
		LicenseData licenseData = new LicenseData();
		licenseData.setUserNum(50000000);
		licenseData.setSpaceSum(1000000000);
		licenseData.setEndTime(new Date());

		String source = generateLicenseData(licenseData);
		log.info("license data ： " + source);
		String secret = key
				+ DateSerializer.dateFormat(licenseData.getEndTime());
		log.info("对称密钥 ： " + secret);

		try {
			String encryptData = DESUtil.encrypt(source, secret);
			log.info("对称密钥加密license data后: " + encryptData);
			Map<String, String> ml = new HashMap<String, String>();
			ml.put("data", encryptData);
			ml.put("secret", secret);
			String license = generateLicense(ml);
			log.info("license : " + license);

			String seed = "1";
			String encrypt_license = RSAUtil.encrypt(license, seed);
			log.info("encrypt_license : " + encrypt_license);
			String decrypt_license = RSAUtil.decrypt(encrypt_license, seed);
			log.info("decrypt_license : " + decrypt_license);

			@SuppressWarnings("unchecked")
			Map<String, String> decrypt_eicense_obj = generateLicense(
					decrypt_license, Map.class);
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

	public static String generateLicenseData(LicenseData license) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(license);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String generateLicense(Map<String, String> license) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(license);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static <T> T generateLicense(String content, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(content, clazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
