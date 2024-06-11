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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class StringMapUtils {

    public static String joinString(Map<String, String> map, String delimiter, String connector) {
        return joinString(map, delimiter, connector, null, true, true);
    }

    public static String joinString(Map<String, String> map, String delimiter, String connector,
        Set<String> excludeKeys) {
        return joinString(map, delimiter, connector, excludeKeys, true, true);
    }

    public static String joinString(Map<String, String> map, String delimiter, String connector,
        Set<String> excludeKeys, boolean excludeEmptyValue, boolean sort) {
        boolean excludeKey = (excludeKeys != null && !excludeKeys.isEmpty());
        Map<String, String> tmpMap = map;
        if (sort) {
            tmpMap = new TreeMap<>(map);
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : tmpMap.entrySet()) {
            String key = entry.getKey();
            if (excludeKey && excludeKeys.contains(key)) { // 排除要排除的键
                continue;
            }
            String value = entry.getValue();
            if (!StringUtils.isNoneBlank(value) && excludeEmptyValue) { // 排除空字符串
                continue;
            }
            builder.append(key).append(connector).append(value).append(delimiter);
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1); // 删除最后的分隔符
        }
        String toString = builder.toString();
        log.debug("拼接字符串结果: {}", toString);
        return toString;
    }

    public static Map<String, String> splitToMap(String string, String delimiter, String connector) {
        String[] strings = StringUtils.splitByWholeSeparatorPreserveAllTokens(string, delimiter);
        Map<String, String> map = new LinkedHashMap<>(strings.length);
        for (String s : strings) {
            String[] entry = StringUtils.splitByWholeSeparatorPreserveAllTokens(s, connector);
            if (entry.length == 2) {
                map.put(entry[0], entry[1]);
            }
        }
        return map;
    }
}
