package encrypt;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FtpClientUtil;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author jinzhimin
 * @description:
 *   HMAC(散列消息身份验证码: Hashed Message Authentication Code)
 *   它不是散列函数，而是采用散列函数（MD5 or 或SHA）与共享密钥一起使用的消息身份验证机制。
 *   按照散列函数的不同，可以有如下实现：
 *   Hmac_MD5，Hmac_sha1，Hmac_sha224，Hmac_sha256，Hmac_sha384，Hmac_sha512。
 */
public class HmacDemo {
    private static final Logger logger = LoggerFactory.getLogger(FtpClientUtil.class);

    private static final String ENCODING = "UTF-8";

    /**
     * 使用 HMAC-SHA1 签名方法对encryptText进行签名
     * @param encryptText 被签名的字符串
     * @param encryptKey  密钥
     * @return 字节数组
     * @throws Exception
     */
    public static byte[] hmacSha1Encrypt(String encryptText, String encryptKey) throws Exception {
        String macName = "HmacSHA1";

        byte[] data = encryptKey.getBytes(ENCODING);
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, macName);
        // 生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance(macName);
        // 用给定密钥初始化 Mac 对象
        mac.init(secretKey);

        byte[] text = encryptText.getBytes(ENCODING);
        // 完成 Mac 操作
        return mac.doFinal(text);
    }

    /**
     * 字节数组转成16进制表示格式的字符串
     *
     * @param byteArray
     *            需要转换的字节数组
     * @return 16进制表示格式的字符串
     **/
    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1) {
            throw new IllegalArgumentException("this byteArray must not be null or empty");
        }

        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10) {
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString().toLowerCase();
    }

    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString
     *            16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] toByteArray(String hexString) {
        if (StringUtils.isEmpty(hexString)) {
            throw new IllegalArgumentException("this hexString must not be empty");
        }

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            // 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    public static void testEncrypt(){
        String text = "345";
        String key = "123";

        try {
            byte[] byteContent = hmacSha1Encrypt(text, key);
            String encrypt = new String(byteContent);
            logger.info("bytes obj, " + byteContent);
            logger.info("encrypt str, " + encrypt);
            logger.info("encrypt hex, " + toHexString(byteContent));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        testEncrypt();
    }

}
