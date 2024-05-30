package top.sephy.infra.mybatis.query;

public interface QueryContextExtractor {

    QueryContext extract(Object object);
}
