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

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public abstract class RestTemplateUtils {

    private static final Map<String, String> FORM_URLENCODED_HEADER;

    static {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        FORM_URLENCODED_HEADER = headers.toSingleValueMap();
    }

    public static HttpEntity buildFormEntity(Object body) {
        return buildEntity(body, FORM_URLENCODED_HEADER);
    }

    public static HttpEntity buildEntity(Object body, Map<String, String> headersMap) {
        MultiValueMap headers = mapToMultiValueMap(headersMap);
        if (body instanceof MultiValueMap) {
            return new HttpEntity(body, headers);
        }
        if (body instanceof Map) {
            return new HttpEntity(mapToMultiValueMap((Map)body), headers);
        }
        return new HttpEntity(body, headers);
    }

    public static MultiValueMap<String, String> mapToMultiValueMap(Map<String, String> map) {
        MultiValueMap multiValueMap = null;
        if (!CollectionUtils.isEmpty(map)) {
            multiValueMap = new LinkedMultiValueMap(map.size());
            for (Map.Entry<String, String> entry : map.entrySet()) {
                multiValueMap.add(entry.getKey(), entry.getValue());
            }
        }
        return multiValueMap;
    }
}
