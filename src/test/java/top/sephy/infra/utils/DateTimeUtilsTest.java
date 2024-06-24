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

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

class DateTimeUtilsTest {

    @Test
    public void timezone() {
        LocalDateTime localDateTime = LocalDateTime.of(1987, 5, 1, 0, 0, 0);
        System.out.println(JacksonUtils.toJson(new LocalDateTimeWithZone(localDateTime, localDateTime,
            localDateTime.toLocalDate(), localDateTime.toLocalDate())));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocalDateTimeWithZone {

        @JsonFormat(pattern = DateTimeUtils.PATTERN_DATE_TIME, timezone = "GMT+8")
        private LocalDateTime ldt1;

        @JsonFormat(pattern = DateTimeUtils.PATTERN_DATE_TIME, timezone = "Asia/Shanghai")
        private LocalDateTime ldt2;

        @JsonFormat(pattern = DateTimeUtils.PATTERN_DATE, timezone = "GMT+8")
        private LocalDate ld1;

        @JsonFormat(pattern = DateTimeUtils.PATTERN_DATE, timezone = "Asia/Shanghai")
        private LocalDate ld2;
    }
}