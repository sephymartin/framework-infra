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
package top.sephy.infra.mybatis.query;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import lombok.Getter;
import top.sephy.infra.utils.DateTimeUtils;

public enum ConverterStrategy implements Converter {

    /**
     * 保留原样, 原汁原味
     */
    RETAIN_VALUE("RETAIN_VALUE") {
        @Override
        public Object convert(Object val) {
            return val;
        }
    },

    /**
     * 去除字符串两端的空格, 如果为空字符串, 则返回null
     */
    TRIM_STRING_TO_NULL("TRIM_STRING_TO_NULL") {
        @Override
        public Object convert(Object val) {
            if (val instanceof String) {
                return StringUtils.trimToNull((String)val);
            }
            return val;
        }
    },

    /**
     * 左模糊查询
     */
    LEFT_LIKE_STRING("LEFT_LIKE_STRING") {
        @Override
        public Object convert(Object val) {
            if (val instanceof String) {
                if (StringUtils.isBlank((String)val)) {
                    return null;
                }
                return "%" + (String)val;
            }
            return val;
        }
    },

    /**
     * 右模糊查询
     */
    RIGHT_LIKE_STRING("RIGHT_LIKE_STRING") {
        @Override
        public Object convert(Object val) {
            if (val instanceof String) {
                if (StringUtils.isBlank((String)val)) {
                    return null;
                }
                return val + "%";
            }
            return val;
        }
    },

    /**
     * 模糊查询
     */
    LIKE_STRING("LIKE_STRING") {
        @Override
        public Object convert(Object val) {
            if (val instanceof String) {
                if (StringUtils.isBlank((String)val)) {
                    return null;
                }
                return "%" + (String)val + "%";
            }
            return val;
        }
    },
    TO_DATETIME("TO_DATETIME") {
        @Override
        public Object convert(Object val) {
            if (val == null) {
                return null;
            }
            LocalDateTime localDateTime = null;
            if (val instanceof Date) {
                localDateTime = DateTimeUtils.toLocalDateTime((Date)val);
            } else if (val instanceof Long) {
                localDateTime = DateTimeUtils.toLocalDateTime(new Date((Long)val));
            } else if (val instanceof LocalDateTime) {
                localDateTime = (LocalDateTime)val;
            } else if (val instanceof LocalDate) {
                localDateTime = ((LocalDate)val).atStartOfDay();
            } else {
                throw new IllegalArgumentException(
                    "不支持 " + val.getClass().getCanonicalName() + " 到 java.lang.Date 的类型转换");
            }
            return localDateTime;
        }
    },

    /**
     * 一天的结束时间
     */
    TO_DATE_END("TO_DATE_END") {
        @Override
        public Object convert(Object val) {
            if (val == null) {
                return null;
            }
            LocalDateTime localDateTime = null;
            if (val instanceof Date) {
                localDateTime = DateTimeUtils.toLocalDateTime((Date)val);
            } else if (val instanceof Long) {
                localDateTime = DateTimeUtils.toLocalDateTime(new Date((Long)val));
            } else if (val instanceof LocalDateTime) {
                localDateTime = (LocalDateTime)val;
            } else if (val instanceof LocalDate) {
                localDateTime = ((LocalDate)val).atStartOfDay();
            } else {
                throw new IllegalArgumentException(
                    "不支持 " + val.getClass().getCanonicalName() + " 到 java.lang.Date 的类型转换");
            }
            return localDateTime.with(LocalTime.MAX);
        }
    },

    /**
     * 一天的开始时间
     */
    TO_DATE_START("TO_DATE_START") {
        @Override
        public Object convert(Object val) {
            if (val == null) {
                return null;
            }
            LocalDateTime localDateTime = null;
            if (val instanceof Date) {
                localDateTime = DateTimeUtils.toLocalDateTime((Date)val);
            } else if (val instanceof Long) {
                localDateTime = DateTimeUtils.toLocalDateTime(new Date((Long)val));
            } else if (val instanceof LocalDateTime) {
                localDateTime = (LocalDateTime)val;
            } else if (val instanceof LocalDate) {
                localDateTime = ((LocalDate)val).atStartOfDay();
            } else {
                throw new IllegalArgumentException(
                    "不支持 " + val.getClass().getCanonicalName() + " 到 java.lang.Date 的类型转换");
            }
            return localDateTime.with(LocalTime.MIN);
        }
    },

    TO_DIM_DATE("DIM_DATE") {
        @Override
        public Object convert(Object val) {
            if (val instanceof Date) {
                return Integer.valueOf(new SimpleDateFormat("yyyyMMdd").format((Date)val));
            } else if (val instanceof Long) {
                return Integer.valueOf(new SimpleDateFormat("yyyyMMdd").format(new Date((Long)val)));
            }
            return null;
        }
    },

    DEFAULT("DEFAULT") {
        @Override
        public Object convert(Object val) {
            if (val instanceof String) {
                return ConverterStrategy.TRIM_STRING_TO_NULL.convert(val);
            } else if (val instanceof Date || val instanceof LocalDate || val instanceof LocalDateTime) {
                return ConverterStrategy.TO_DATE_END.convert(val);
            } else {
                return ConverterStrategy.RETAIN_VALUE.convert(val);
            }
        }
    },;

    @Getter
    private String name;

    ConverterStrategy(String name) {
        this.name = name;
    }
}
