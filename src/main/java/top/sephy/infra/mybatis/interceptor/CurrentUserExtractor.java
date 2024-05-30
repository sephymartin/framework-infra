package top.sephy.infra.mybatis.interceptor;

public interface CurrentUserExtractor<T> {

    T getCurrentUserId();

    default String getCurrentUserName() {
        return "";
    }
}
