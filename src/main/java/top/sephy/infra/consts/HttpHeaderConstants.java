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
package top.sephy.infra.consts;

public abstract class HttpHeaderConstants {

    public static final String HEADER_X_FORWARDED_FOR = "x-forwarded-for";

    public static final String HEADER_X_REAL_IP = "x-real-ip";

    public static final String HEADER_AUTH_USERID = "auth-userid";

    public static final String HEADER_AUTH_USERNAME = "auth-username";

    public static final String HEADER_OAUTH_CLIENTID = "oauth-clientid";

    public static final String HEADER_TRACE_ID = "trace-id";

    public static final String HEADER_TRACE_SKIP = "trace-skip";

    public static final String HEADER_HOST = "host";

    public static final String HEADER_X_ORIGINAL_SCHEME = "x-original-scheme";

    public static final String HEADER_X_ORIGINAL_PORT = "x-original-port";

    public static final String HEADER_X_ORIGINAL_URI = "x-original-uri";

    public static final String HEADER_USER_AGENT = "user-agent";

    public static final String HEADER_BIZ_TYPE = "biz-type";
}
