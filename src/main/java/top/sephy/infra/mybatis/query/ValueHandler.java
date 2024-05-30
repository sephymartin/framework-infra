package top.sephy.infra.mybatis.query;

public interface ValueHandler<T, E> {
    T handleValue(E val);
}
