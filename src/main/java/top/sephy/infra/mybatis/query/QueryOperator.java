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

import java.util.HashMap;
import java.util.Map;

public enum QueryOperator {

    NULL(false, "IS NULL", "nu", false),

    NOT_NULL(false, "IS NOT NULL", "nnu", false),

    EQ(true, "=", "eq", false),

    NE(true, "!=", "ne", false),

    LIKE(true, "like", "lk", false),

    NOT_LIKE(true, "not like", "nlk", false),

    GT(true, ">", "gt", false),

    GE(true, ">=", "ge", false),

    LT(true, "<", "lt", false),

    LE(true, "<=", "le", false),

    IN(true, "in", "in", true),

    NOT_IN(true, "not in", "nin", true);

    private boolean binary;

    private String operator;

    private String symbol;

    private boolean iterable;

    QueryOperator(boolean binary, String operator, String symbol, boolean iterable) {
        this.binary = binary;
        this.operator = operator;
        this.symbol = symbol;
        this.iterable = iterable;
    }

    public boolean isBinary() {
        return binary;
    }

    public boolean isIterable() {
        return iterable;
    }

    public String getOperator() {
        return operator;
    }

    public String getSymbol() {
        return symbol;
    }

    private static final Map<String, QueryOperator> map;

    static {
        map = new HashMap<>();
        for (QueryOperator queryOperator : QueryOperator.values()) {
            map.put(queryOperator.symbol, queryOperator);
        }
    }

    public static QueryOperator fromSymbol(String symbol) {
        return map.get(symbol);
    }
}
