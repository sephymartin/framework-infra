package top.sephy.infra.utils;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public abstract class TripleDESUtils {

    private static final String ALGORITHM = "DESede";
    private static final String ENCODING = "UTF-8";
    public static final String DESEDE_ECB_PKCS_5_PADDING = "DESede/ECB/PKCS5Padding";

    public static String encrypt(String plainText, String base64Key) {
        try {
            SecretKey keySpec = new SecretKeySpec(Base64.getDecoder().decode(base64Key), ALGORITHM);
            Cipher cipher = Cipher.getInstance(DESEDE_ECB_PKCS_5_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] bts = cipher.doFinal(plainText.getBytes(ENCODING));
            return Base64.getEncoder().encodeToString(bts);
        } catch (Exception e) {
            throw new RuntimeException("3DES 加密失败", e);
        }
    }

    public static String encrypt(byte[] key, String plainText) {
        try {
            SecretKey keySpec = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(DESEDE_ECB_PKCS_5_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] bts = cipher.doFinal(plainText.getBytes(ENCODING));
            return Base64.getEncoder().encodeToString(bts);
        } catch (Exception e) {
            throw new RuntimeException("3DES 加密失败", e);
        }
    }

    public static String decrypt(byte[] key, String cipherText) {

        try {
            SecretKey keySpec = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] bts = cipher.doFinal(Base64.getDecoder().decode(cipherText));

            return new String(bts, ENCODING);
        } catch (Exception e) {
            throw new RuntimeException("3DES 解密失败", e);
        }
    }
}
