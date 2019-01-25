package encrypt;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;  

/**
 * @author jinzhimin
 * @description:
*/
public class EncryptUtil {
	
    /**
     * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
     */
    private static String AES_Key = "0123456WhoAreYou";  
    private static String ivParameter = "abcd789WhoAreYou";  
	
    // 密钥，长度需要为8的倍数
    private static String DES_KEY = "1234567WhoAreYou";
    private static String DES = "DES";
    
	 /**  
     * BASE64解密  
     *   
     * @param value
     * @return  
     * @throws Exception  
     */  
    public static String decryptBASE64(String value) throws Exception {  
    	String result = "";
    	byte[] byteValue = (new BASE64Decoder()).decodeBuffer(value);
    	result = new String(byteValue);
    	
        return result;   
    }
    
    /**  
     * BASE64加密  
     *   
     * @param value
     * @return  
     * @throws Exception  
     */  
    public static String encryptBASE64(String value) throws Exception {   
    	String result = "";
    	byte[] byteValue = value.getBytes();
    	result = (new BASE64Encoder()).encodeBuffer(byteValue); 
        
        return result;
    }
    
    /**
     * 加密
     * @param value
     * @return
     */
    public static String encryptAES(String value){  
        String result = "";
        try {  
        	byte[] rawAES = AES_Key.getBytes();
        	SecretKeySpec skeySpec = new SecretKeySpec(rawAES, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // 使用CBC模式，需要一个向量iv，可增加加密算法的强度  
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            
            byte[] encrypted = cipher.doFinal(value.getBytes("utf-8")); 
            // 此处使用BASE64做转码。  
            result = new BASE64Encoder().encode(encrypted);
        } catch (Exception e) {
        	
        }   
        return result;  
    }  
  
    /**
     * 解密
     * @param value
     * @return
     */
    public static String decryptAES(String value){  
    	String originalString = null;
        try {  
        	byte[] rawAES = AES_Key.getBytes();
        	SecretKeySpec skeySpec = new SecretKeySpec(rawAES, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());  
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            // 先用base64解密
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(value);
            byte[] original = cipher.doFinal(encrypted1);  
            originalString = new String(original, "utf-8");  
        } catch (Exception e) {  
            
        }
        
        return originalString; 
    }

    public static byte[] useDES(byte[] src, byte[] key, int mode) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        // 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);

        SecretKey secretKey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密匙初始化Cipher对象
        cipher.init(mode, secretKey, sr);

        // 现在，获取数据并加密
        // 正式执行加密操作
        return cipher.doFinal(src);
    }
    
    public static byte[] encryptDES(byte[] src, byte[] key) throws Exception {
        return useDES(src, key, Cipher.ENCRYPT_MODE);
    }
 
    /**
     * 解密
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     * @throws Exception
     */
    public static byte[] decryptDES(byte[] src, byte[] key) throws Exception {
        return useDES(src, key, Cipher.DECRYPT_MODE);
    }
 
    /**
     * 密码解密
     * @param data
     * @return
     * @throws Exception
     */
    public final static String decryptDES(String data) {
        try {
            return new String(decryptDES(hex2byte(data.getBytes()), DES_KEY.getBytes()));
        } catch (Exception e) {
 
        }
        return null;
    }
 
    /**
     * 密码加密
     * @param password
     * @return
     * @throws Exception
     */
    public final static String encryptDES(String password) {
        try {
            return byte2hex(encryptDES(password.getBytes(), DES_KEY.getBytes()));
        } catch (Exception e) {
 
        }
        return null;
    }
 
    /**
     * 二行制转字符串
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1){
                hs = hs + "0" + stmp;
            }else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }
 
    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }
    
    
    public static void main(String[] args){
    	String value = "20171122_123ds-dfase-eead2";
    	
		try {
            String enValue = encryptBASE64(value);
            System.out.println("----加密后的数据----" + enValue);
            
            String deValue = decryptBASE64(enValue);
            System.out.println("----解密后的数据----" + deValue);
            
            enValue = encryptAES(value);
            System.out.println("----加密后的数据----" + enValue);
            
            deValue = decryptAES(enValue);
            System.out.println("----解密后的数据----" + deValue);
            
            enValue = encryptDES(value);
            System.out.println("----加密后的数据----" + enValue);
            
            deValue = decryptDES(enValue);
            System.out.println("----解密后的数据----" + deValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}
