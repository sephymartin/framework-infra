package top.sephy.infra.auth;

public interface AuthenticationAdapter<T> {

    /**
     * 获取当前用户 ID
     * 
     * @return
     */
    T getCurrentUserId();

    /**
     * 获取当前用户信息
     * 
     * @return
     */
    AuthenticationInfo getCurrentUserInfo();
}
