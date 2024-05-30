package top.sephy.infra.mybatis.query;

import lombok.Data;

@Data
public class QueryExpression<T> {

    private String field;

    private QueryOperator op;

    private T val;
}
