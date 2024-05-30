package top.sephy.infra.auth;

import java.util.Map;

public interface AuthenticationInfo<T> {

    T getUserId();

    /**
     * 获取当前用户名称
     * 
     * @return
     */
    String getNickname();

    Map<String, Object> getOtherInfo();

}
