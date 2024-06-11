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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import top.sephy.infra.consts.HttpHeaderConstants;

public abstract class ServletHttpRequestUtils {

    private static final String HEADER_X_FORWARDED_FOR = HttpHeaderConstants.HEADER_X_FORWARDED_FOR;

    private static final String HEADER_X_REAL_IP = HttpHeaderConstants.HEADER_X_REAL_IP;

    public static String getIpAddress(HttpServletRequest request) {
        String ip = null;
        String header = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (StringUtils.hasText(header)) {
            int index = header.indexOf(',');
            if (index > 0) {
                ip = StringUtils.trimAllWhitespace(header.substring(0, index));
            } else {
                ip = StringUtils.trimAllWhitespace(header);
            }

            if ("unknown".equalsIgnoreCase(ip)) {
                ip = null;
            }
        }

        if (!StringUtils.hasText(ip)) {
            ip = StringUtils.trimAllWhitespace(request.getHeader(HEADER_X_REAL_IP));
        }

        if (!StringUtils.hasText(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /**
     * 获取请求的原始完整请求路径
     *
     * @param request
     * @return
     */
    public static String getOriginalUri(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        String scheme = request.getHeader(HttpHeaderConstants.HEADER_X_ORIGINAL_SCHEME);
        if (scheme == null) {
            scheme = request.getScheme();
        }
        sb.append(scheme).append("://");
        String host = request.getHeader(HttpHeaderConstants.HEADER_HOST);
        if (host == null) {
            host = request.getServerName();
        }
        sb.append(host);
        String port = request.getHeader(HttpHeaderConstants.HEADER_X_ORIGINAL_PORT);
        if (port == null) {
            port = String.valueOf(request.getServerPort());
        }
        if ("https".equalsIgnoreCase(scheme) && !"443".equals(port)) {
            sb.append(":").append(port);
        } else if ("http".equalsIgnoreCase(scheme) && !"80".equals(port)) {
            sb.append(":").append(port);
        }
        String uri = request.getHeader(HttpHeaderConstants.HEADER_X_ORIGINAL_URI);
        if (uri == null) {
            uri = request.getRequestURI();
            if (request.getQueryString() != null) {
                uri = uri + "?" + request.getQueryString();
            }
        }
        sb.append(uri);
        return sb.toString();
    }

    public static Map<String, String> getParamsMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String parameter = StringUtils.trimWhitespace(request.getParameter(name));
            if (StringUtils.hasText(parameter)) {
                map.put(name, parameter);
            }
        }
        return map;
    }

    /**
     * 判断是否是 json 请求
     *
     * @param request
     * @return
     */
    public static boolean isJsonApplicationRequest(HttpServletRequest request) {
        return request.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE);
    }
}
