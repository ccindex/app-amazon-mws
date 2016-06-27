package amazon.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import play.libs.Json;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Title: GlobalKit.java
 *
 * @author Lc
 *
 * @date 2016年4月20日 下午5:58:34
 */
public class GlobalKit {

	public static SimpleDateFormat reportFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static XMLGregorianCalendar convertToXMLGregorianCalendar(String date) throws Exception {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date) );
		XMLGregorianCalendar gc = null;
		try {
			gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gc;
	}

	public static Date convertToDate(XMLGregorianCalendar cal) throws Exception {
		return cal.toGregorianCalendar().getTime();
	}
	
	public static Map<String, String> authMap(String authJson) {
		JsonNode jsonNode = Json.parse(authJson);
		String accessKey = OrderKit.toValue(jsonNode, "access_key_id");
		String secretKey = OrderKit.toValue(jsonNode, "secret_access_key");
		String appName = OrderKit.toValue(jsonNode, "app_name");
		String appVersion = OrderKit.toValue(jsonNode, "app_version");
		String merchantId = OrderKit.toValue(jsonNode, "merchant_id");
		String marketplaceId = OrderKit.toValue(jsonNode, "marketplace_id");
		String serviceURL = OrderKit.toValue(jsonNode, "service_url");
		String mwsAuthToken = OrderKit.toValue(jsonNode, "mws_auth_token"); // TODO 目前该值暂未使用
		String developerId = OrderKit.toValue(jsonNode, "developer_id");    // TODO 目前该值暂未使用

		StringBuilder inf = new StringBuilder();
		inf.append(GlobalKit.newline());
		inf.append("-------------------------------------------------------").append(GlobalKit.newline());
		inf.append("accessKey: " + accessKey).append(GlobalKit.newline());
		inf.append("secretKey: " + secretKey).append(GlobalKit.newline());
		inf.append("appName: " + appName).append(GlobalKit.newline());
		inf.append("appVersion: " + appVersion).append(GlobalKit.newline());
		inf.append("merchantId: " + merchantId).append(GlobalKit.newline());
		inf.append("marketplaceId: " + marketplaceId).append(GlobalKit.newline());
		inf.append("serviceURL: " + serviceURL).append(GlobalKit.newline());
		inf.append("mwsAuthToken: " + mwsAuthToken).append(GlobalKit.newline());
		inf.append("developerId: " + developerId).append(GlobalKit.newline());
		inf.append("-------------------------------------------------------").append(GlobalKit.newline());
		APLogger.info(inf.toString());

		Map<String, String> map = new HashMap<String, String>();
		map.put("accessKey", accessKey);
		map.put("secretKey", secretKey);
		map.put("appName", appName);
		map.put("appVersion", appVersion);
		map.put("merchantId", merchantId);
		map.put("marketplaceId", marketplaceId);
		map.put("serviceURL", serviceURL);
		map.put("mwsAuthToken", mwsAuthToken);
		map.put("developerId", developerId);
		
		return map;
	}
	
	/**
	 * 获取系统换行符
	 */
	public static String newline() {
		return System.getProperty("line.separator");
	}
	
	/**
	 * 获取当前系统时间-3分钟
	 */
	public static String getCreatedBefore() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, -3);
		return dateFormat.format( c.getTime() );
	}
	
	public static String getCurrentTime() {
		return dateFormat.format(new Date());
	}

	public static String genReportNum() {
		return reportFormat.format(new Date());
	}

	/**
	 * Consume the stream and return its Base-64 encoded MD5 checksum.
	 * 
	 * <br/>
	 * Tips: 对下载的报告计算 MD5 校验和
	 */
	public static String computeContentMD5Header(InputStream inputStream) {
		// Consume the stream to compute the MD5 as a side effect.
		DigestInputStream s = null;
		try {
			s = new DigestInputStream(inputStream, MessageDigest.getInstance("MD5"));
			// drain the buffer, as the digest is computed as a side-effect
			byte[] buffer = new byte[8192];
			while (s.read(buffer) > 0);
			return new String(org.apache.commons.codec.binary.Base64.encodeBase64(s.getMessageDigest().digest()), "UTF-8");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (s != null) {
				try {
					s.close();
					s = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Calculate content MD5 header values for feeds stored on disk.
	 * 
	 * <br/>
	 * Tips: 对提交到亚马逊的上传数据计算 Content-MD5
	 */
	public static String computeContentMD5HeaderValue(FileInputStream fis) throws IOException, NoSuchAlgorithmException {

		DigestInputStream dis = new DigestInputStream(fis, MessageDigest.getInstance("MD5"));

		byte[] buffer = new byte[8192];
		while (dis.read(buffer) > 0);

		String md5Content = new String(org.apache.commons.codec.binary.Base64.encodeBase64(dis.getMessageDigest().digest()));

		// Effectively resets the stream to be beginning of the file
		// via a FileChannel.
		fis.getChannel().position(0);

		return md5Content;
	}
	
	/**
	 * Ebean的原生SQL查询方式.
	 */
	public static <T> Query<T> queryOfRawSql(Class<T> beanType, String sql) {
		return Ebean.find(beanType).setRawSql(RawSqlBuilder.parse(sql).create());
	}
	
	private static Map<String, String> encodeMap = new HashMap<>();
	private static Map<String, String> urlMap = new HashMap<>();
	
	static {
		// 字符编码
		encodeMap.put("JP", "Shift_Jis");

		// URL
		urlMap.put("", "https://developer.amazonservices.ca"); // 加拿大
		urlMap.put("", "https://developer.amazonservices.com"); // 美国
		urlMap.put("", "https://developer.amazonservices.de"); // 德国
		urlMap.put("", "https://developer.amazonservices.es"); // 西班牙
		urlMap.put("", "https://developer.amazonservices.fr"); // 法国
		urlMap.put("", "https://developer.amazonservices.in"); // 印度
		urlMap.put("", "https://developer.amazonservices.it"); // 意大利
		urlMap.put("", "https://developer.amazonservices.co.uk"); // 英国
		urlMap.put("", "https://developer.amazonservices.jp"); // 日本
		urlMap.put("", "https://developer.amazonservices.com.cn"); // 中国
	}
	
	public static String getEncode(String key) {
		return encodeMap.get(key) == null ? "UTF-8" : encodeMap.get(key);
	}

}
