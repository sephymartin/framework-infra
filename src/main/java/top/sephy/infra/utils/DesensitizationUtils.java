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

public abstract class DesensitizationUtils {

    public static String chineseName(String name) {
        if (name == null) {
            return null;
        }
        if (name.length() <= 1) {
            return "*";
        }
        if (name.length() == 2) {
            return name.substring(0, 1) + "*";
        }
        char[] cs = name.toCharArray();
        for (int i = 1; i < cs.length - 1; i++) {
            cs[i] = '*';
        }
        return new String(cs);
    }

    public static String idCardNum(String num) {
        if (num == null) {
            return null;
        }
        if (num.length() <= 10) {
            return num;
        }
        char[] cs = num.toCharArray();
        for (int i = 6; i < cs.length - 4; i++) {
            cs[i] = '*';
        }
        return new String(cs);
    }

    public static String fixedPhone(String num) {
        if (num == null) {
            return null;
        }
        if (num.length() <= 4) {
            return num;
        }
        char[] cs = num.toCharArray();
        for (int i = 0; i < cs.length - 4; i++) {
            cs[i] = '*';
        }
        return new String(cs);
    }

    public static String mobilePhone(String num) {
        if (num == null) {
            return null;
        }
        if (num.length() <= 7) {
            return num;
        }
        char[] cs = num.toCharArray();
        for (int i = 3; i < cs.length - 4; i++) {
            cs[i] = '*';
        }
        return new String(cs);
    }

    public static String address(String address, int sensitiveSize) {
        if (address == null) {
            return null;
        }
        if (address.length() <= sensitiveSize + 2) {
            return address;
        }
        char[] cs = address.toCharArray();
        for (int i = sensitiveSize; i < cs.length - 2; i++) {
            cs[i] = '*';
        }
        return new String(cs);
    }

    public static String email(String email) {
        if (email == null) {
            return null;
        }
        int index = email.indexOf('@');
        if (index <= 1) {
            return email;
        }
        char[] cs = email.toCharArray();
        for (int i = 0; i < index - 1; i++) {
            cs[i] = '*';
        }
        return new String(cs);
    }

    public static String bankCard(String num) {
        if (num == null) {
            return null;
        }
        if (num.length() <= 8) {
            return num;
        }
        char[] cs = num.toCharArray();
        for (int i = 0; i < cs.length - 8; i++) {
            cs[i] = '*';
        }
        return new String(cs);
    }

    public static String password(String password) {
        if (password == null) {
            return null;
        }
        return "******";
    }

    public static String cnapsCode(String code) {
        if (code == null) {
            return null;
        }
        if (code.length() <= 6) {
            return code;
        }
        char[] cs = code.toCharArray();
        for (int i = 0; i < cs.length - 6; i++) {
            cs[i] = '*';
        }
        return new String(cs);
    }

    public static String creditCode(String code) {
        if (code == null) {
            return null;
        }
        if (code.length() <= 9) {
            return code;
        }
        char[] cs = code.toCharArray();
        for (int i = 0; i < cs.length - 9; i++) {
            cs[i] = '*';
        }
        return new String(cs);
    }
}
