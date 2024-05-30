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
