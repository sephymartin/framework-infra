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
