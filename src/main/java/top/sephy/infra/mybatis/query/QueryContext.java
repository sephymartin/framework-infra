package top.sephy.infra.mybatis.query;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class QueryContext {

    List<QueryExpression<Object>> expressions;

    Map<String, QueryExpression<Object>> expressionMap;

    public QueryContext merge(QueryContext other) {
        if (other == null) {
            return this;
        }
        if (other.getExpressions() != null) {
            this.getExpressions().addAll(other.getExpressions());
        }
        if (other.getExpressionMap() != null) {
            this.getExpressionMap().putAll(other.getExpressionMap());
        }
        return this;
    }
}
