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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.apache.commons.lang3.StringUtils;

public abstract class KeyPairUtils {

    private static final KeyFactory RSA_KEY_FACTORY;

    static {
        try {
            RSA_KEY_FACTORY = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKey loadX509PublicKey(String key) {
        key = removeNoneKeyString(key);
        byte[] bytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        try {
            return RSA_KEY_FACTORY.generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("加载公钥失败", e);
        }
    }

    public static PublicKey loadX509CertPubKey(String key) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            String src = removeNoneKeyString(key);
            Certificate certificate =
                certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(src)));
            return certificate.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("加载公钥失败", e);
        }
    }

    public static PrivateKey loadPKCS8PrivateKey(String key) {
        key = removeNoneKeyString(key);
        byte[] bytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        try {
            return RSA_KEY_FACTORY.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("加载私钥失败", e);
        }
    }

    private static String removeNoneKeyString(String key) {
        // key = StringUtils.replace(key, "\n", "\r");
        try (BufferedReader br = new BufferedReader(new StringReader(key))) {
            String line = null;
            StringBuilder keyBuffer = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("-")) {
                    keyBuffer.append(StringUtils.trim(line));
                }
            }
            return keyBuffer.toString();
        } catch (IOException e) {
            throw new RuntimeException("读取密钥字符串失败", e);
        }
    }
}
