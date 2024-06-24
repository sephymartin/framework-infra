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
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 日期转换工具类
 *
 * @author sephy
 */
public abstract class DateTimeUtils {

    public static final int UNKNOWN = 0;

    public static final int MONTH_MULTI_POW = 100;

    public static final int YEAR_MULTI_POW = 10000;

    public static final String TIME_ZONE_BEIJING = "Asia/Shanghai";

    public static final String PATTERN_DATE = "yyyy-MM-dd";

    public static final String PATTERN_TIME = "HH:mm:ss";

    public static final LocalDateTime MYSQL_MIN_DATETIME = LocalDateTime.of(1000, 1, 1, 0, 0, 0);

    public static final String PATTERN_DATE_TIME = PATTERN_DATE + " " + PATTERN_TIME;

    public static int toDateKey(Date date) {
        if (date == null) {
            return 0;
        }
        return Integer.parseInt(DateFormatUtils.format(date, "yyyyMMdd"));
    }

    public static int toDateKey(LocalDate localDate) {
        return localDate.getYear() * YEAR_MULTI_POW + localDate.getMonthValue() * MONTH_MULTI_POW
            + localDate.getDayOfMonth();
    }

    public static int computeDayInterval(int lesserDateKey, int greaterDateKey) {
        return (int)ChronoUnit.DAYS.between(toLocalDate(lesserDateKey), toLocalDate(greaterDateKey));
    }

    public static LocalDate toLocalDate(int dateKey) {
        int year = dateKey / YEAR_MULTI_POW;
        int month = (dateKey % YEAR_MULTI_POW) / MONTH_MULTI_POW;
        int day = dateKey % MONTH_MULTI_POW;
        return LocalDate.of(year, month, day);
    }

    public static int toTimeKey(Date date) {
        return date == null ? UNKNOWN : Integer.parseInt(DateFormatUtils.format(date, "HHmmss"));
    }

    public static int toTimeKey(LocalTime localTime) {
        return localTime == null ? UNKNOWN
            : localTime.getHour() * YEAR_MULTI_POW + localTime.getMinute() * MONTH_MULTI_POW + localTime.getSecond();
    }

    public static LocalDate toLocalDate(Date date) {
        return toLocalDate(date, ZoneId.systemDefault());
    }

    public static LocalDate toLocalDate(Date date, ZoneId zoneId) {
        return date.toInstant().atZone(zoneId).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

    }

    public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
        return date.toInstant().atZone(zoneId).toLocalDateTime();

    }

    public static Date toDate(LocalDate localDate) {
        return toDate(localDate, ZoneId.systemDefault());
    }

    public static Date toDate(LocalDate localDate, ZoneId zoneId) {
        return Date.from(localDate.atStartOfDay().atZone(zoneId).toInstant());
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalDateTime localDateTime, ZoneId zoneId) {
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }
}
