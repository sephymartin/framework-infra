/*
 * Copyright 2022-2024 sephy.top
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.sephy.infra.utils;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import javax.crypto.Cipher;

import lombok.NonNull;

public abstract class RSAUtils {

    public static final String MD5_WITH_RSA = "MD5withRSA";

    public static final String SHA1_WITH_RSA = "SHA1withRSA";

    public static final String SHA224_WITH_RSA = "SHA224withRSA";

    public static final String SHA256_WITH_RSA = "SHA256withRSA";

    public static final String SHA384_WITH_RSA = "SHA384withRSA";

    public static final String SHA512_WITH_RSA = "SHA512withRSA";

    public static byte[] sign(byte[] data, @NonNull PrivateKey privateKey, @NonNull String algorithm) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception ex) {
            throw new RuntimeException("签名失败", ex);
        }
    }

    public static boolean verify(byte[] data, byte[] signatureData, PublicKey publicKey, String algorithm) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(signatureData);
        } catch (Exception ex) {
            throw new RuntimeException("验签失败", ex);
        }
    }

    public static byte[] encrypt(byte[] rawData, PublicKey pubKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return cipher.doFinal(rawData);
        } catch (Exception e) {
            throw new RuntimeException("RSA加密失败", e);
        }
    }

    public static String encryptToBase64(String plainText, PublicKey pubKey) {
        return Base64.getEncoder().encodeToString(encrypt(plainText.getBytes(StandardCharsets.UTF_8), pubKey));
    }

    public static byte[] decrypt(byte[] rawData, PrivateKey priKey) {

        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return cipher.doFinal(rawData);
        } catch (Exception e) {
            throw new RuntimeException("RSA解密失败", e);
        }
    }

    public static String decryptToString(byte[] cipherData, PrivateKey priKey) {
        return new String(decrypt(cipherData, priKey), StandardCharsets.UTF_8);
    }
}
