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
package top.sephy.infra.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.extern.slf4j.Slf4j;

public class CustomSecurityUserModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(CustomSecurityUser.class, CustomSecurityUserModuleMixIn.class);
    }

    @Slf4j
    public static class CustomSecurityUserModuleMixIn<T> {
        @JsonCreator
        public CustomSecurityUserModuleMixIn(@JsonProperty("username") String username,
            @JsonProperty("password") String password, @JsonProperty("enabled") boolean enabled,
            @JsonProperty("accountNonExpired") boolean accountNonExpired,
            @JsonProperty("credentialsNonExpired") boolean credentialsNonExpired,
            @JsonProperty("accountNonLocked") boolean accountNonLocked,
            @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities,
            @JsonProperty("otherInfo") Map<String, Object> otherInfo) {
            log.info("ViewportSizeMixIn called!");
        }
    }
}
