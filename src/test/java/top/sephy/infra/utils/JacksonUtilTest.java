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

import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.sephy.infra.jackson.annotation.JsonHashId;

public class JacksonUtilTest {

    @Test
    public void testHashId() {
        HashIdObject hashIdObject = new HashIdObject(1L);
        String json = JacksonUtils.toJson(hashIdObject);
        System.out.println(json);
        hashIdObject = JacksonUtils.jsonToObject(json, HashIdObject.class);
        System.out.println(hashIdObject.id);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HashIdObject {

        @JsonHashId
        private Long id;
    }
}
