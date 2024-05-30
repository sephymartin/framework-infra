package top.sephy.infra.utils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import top.sephy.infra.exception.SystemException;

public abstract class SignatureUtils {

    public static final String MD5_WITH_RSA = "MD5withRSA";

    public static final String SHA1_WITH_RSA = "SHA1withRSA";

    public static final String SHA224_WITH_RSA = "SHA224withRSA";

    public static final String SHA256_WITH_RSA = "SHA256withRSA";

    public static final String SHA384_WITH_RSA = "SHA384withRSA";

    public static final String SHA512_WITH_RSA = "SHA512withRSA";

    public static final byte[] sign(byte[] data, PrivateKey privateKey, String algorithm) throws SystemException {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception ex) {
            throw new SystemException("签名失败", ex);
        }
    }

    public static final boolean verify(byte[] data, byte[] signatureData, PublicKey publicKey, String algorithm) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(signatureData);
        } catch (Exception ex) {
            throw new SystemException("验签失败", ex);
        }
    }
}
