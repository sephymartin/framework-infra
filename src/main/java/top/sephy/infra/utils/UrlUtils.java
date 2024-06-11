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

public abstract class UrlUtils {

    static String PATH_SEPARATOR = "/";

    public static String joinPathSegment(String... segments) {
        StringBuilder sb = new StringBuilder();
        for (String segment : segments) {
            if (segment == null || segment.isEmpty()) {
                continue;
            }
            if (segment.startsWith(PATH_SEPARATOR)) {
                segment = segment.substring(1);
            }
            if (segment.endsWith(PATH_SEPARATOR)) {
                segment = segment.substring(0, segment.length() - 1);
            }
            sb.append(PATH_SEPARATOR);
            sb.append(segment);
        }
        return sb.toString();
    }
}
