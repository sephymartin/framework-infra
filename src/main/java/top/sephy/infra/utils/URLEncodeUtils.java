package top.sephy.infra.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public abstract class URLEncodeUtils {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    public static String urlEncodeWithUTF8(String stringToEncode) {
        if (stringToEncode == null) {
            return null;
        } else {
            try {
                return URLEncoder.encode(stringToEncode, UTF_8);
            } catch (UnsupportedEncodingException var2) {
                throw new RuntimeException("Url encode with utf-8 failed.", var2);
            }
        }
    }

    public static String urlDecodeWithUTF8(String stringToDecode) {
        if (stringToDecode == null) {
            return null;
        } else {
            try {
                return URLDecoder.decode(stringToDecode, UTF_8);
            } catch (UnsupportedEncodingException var2) {
                throw new RuntimeException("Url decode with utf-8 failed.", var2);
            }
        }
    }
}
