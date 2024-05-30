package top.sephy.infra.security;

import top.sephy.infra.auth.AuthenticationAdapter;
import top.sephy.infra.auth.AuthenticationInfo;

public abstract class AbstractAuthAdapter<T> implements AuthenticationAdapter<T> {

    @Override
    public T getCurrentUserId() {
        AuthenticationInfo<T> authenticationInfo = getCurrentUserInfo();
        if (authenticationInfo != null) {
            return authenticationInfo.getUserId();
        }
        return null;
    }
}
