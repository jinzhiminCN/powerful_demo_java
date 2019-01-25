package encrypt;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * @author jinzhimin
 * @description:
 */
public class AESUtil {

    private static final String AESTYPE = "AES/ECB/PKCS5Padding";
    private static final String AES = "AES";

    /**
     * 加密
     *
     * @param pwdStr     密钥
     * @param contentStr 内容
     * @return
     */
    public static String encrypt(String pwdStr, String contentStr) {
        String byteArrayToBase64String = null;
        if (pwdStr == null) {
            return byteArrayToBase64String;
        }
        if (contentStr == null) {
            return byteArrayToBase64String;
        }

        try {
            Key key = new SecretKeySpec(pwdStr.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(AESTYPE);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypt = cipher.doFinal(contentStr.getBytes());
            byteArrayToBase64String = byteArrayToBase64String(encrypt);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return byteArrayToBase64String;
    }

    public static String byteArrayToBase64String(byte[] byteArray) {
        if (byteArray.length == 0) {
            return new String();
        }
        byte[] b = Base64.encodeBase64(byteArray);
        return new String(b);
    }

    /**
     * 解密
     *
     * @param keyPwd     密钥
     * @param encryptStr 解密字符串
     * @return
     */
    public static String decrypt(String keyPwd, String encryptStr) {
        byte[] decrypt = null;
        String str = null;
        if (keyPwd == null) {
            return str;
        }
        if (encryptStr == null) {
            return str;
        }

        try {
            Key key = new SecretKeySpec(keyPwd.getBytes(), AES);

            Cipher cipher = Cipher.getInstance(AESTYPE);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodeBase64 = Base64.decodeBase64(encryptStr.getBytes());
            decrypt = cipher.doFinal(decodeBase64);
            str = new String(decrypt);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void main(String[] args) {
        String content = "testhello";
//		String pwdStr = "1234522890345111";
//		String encryptStr = encrypt(pwdStr, content);
//		System.out.println("加密结果：" + encryptStr);
//		
//		String decryptStr = decrypt(pwdStr, encryptStr);
//		System.out.println("解密结果：" + decryptStr);

//		hello(content);
//		System.out.println(content);
//		int a = 1;
//		hello(a);
//		System.out.println(a);

        int[] array = new int[]{-2, 5, 3, -2, -3, 8, 2, 1, -5, 9, -3};
//		test1(array);
//		test2(array);

    }

    public static void test3(int[] array) {
        int length = array.length;

        int[][] maxArray = new int[length][length];
    }

    public static void test2(int[] array) {
        int ans = 0, dp = 0;
        for (int i = 0; i < array.length; i++) {
            if (dp > 0) {
                dp += array[i];
            } else {
                dp = array[i];
            }

            if (dp > ans) {
                ans = dp;
            }
        }
        System.out.println("ans:" + ans + ", dp:" + dp);
    }

    public static void test1(int[] array) {
        int sum = 0;
        int max = 0;
        int currentMax = 0;
        int begin = 0;
        int end = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
            if (sum > 0) {
                currentMax += array[i];
                end = i;
            } else {
                currentMax = 0;
                begin = i + 1;
            }

            if (currentMax > max) {
                max = currentMax;
            }
        }

        System.out.println("max:" + max + ", sum:" + sum +
                ", currentMax:" + currentMax + ", begin:" + begin + ", end:" + end);

    }

    public static void hello(int a) {
        a = 3;
    }

    public static void hello(String test) {
        test = "123";
    }

}
