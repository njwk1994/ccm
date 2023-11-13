package ccm.server.util;

import cn.hutool.crypto.symmetric.AES;

/**
 * AES加密工具
 *
 * @author HuangTao
 * @version 1.0
 * @since 2022/1/24 15:47
 */
public class CCMAESUtils {

    private static final String IV = "1234567890000000";//16位
    /**
     * 密钥
     */
    private static final String KEY = "12345678900000001234567890000000";//32位

    private CCMAESUtils() {
    }

    /**
     * AES-CBC-PKCS7Padding 算法加密
     * <p>CBC模式,PKCS7Padding补码</p>
     *
     * @param input
     * @return
     */
    public static String encryptCBCHex(String input) {
        AES aes = new AES("CBC", "PKCS7Padding", KEY.getBytes(), IV.getBytes());
        return aes.encryptHex(input);
    }

    /**
     * AES-CBC-PKCS7Padding 算法解密
     * <p>CBC模式,PKCS7Padding补码</p>
     *
     * @param input
     * @return
     */
    public static String decryptStr(String input) {
        AES aes = new AES("CBC", "PKCS7Padding", KEY.getBytes(), IV.getBytes());
        return aes.decryptStr(input);
    }

    /**
     * String左对齐
     *
     * @param src
     * @param len
     * @param ch
     * @return
     */
    public static String padLeft(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
        for (int i = src.length(); i < len; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

    /**
     * String右对齐
     *
     * @param src
     * @param len
     * @param ch
     * @return
     */
    public static String padRight(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
        for (int i = 0; i < diff; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

}
