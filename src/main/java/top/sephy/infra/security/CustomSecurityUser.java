package top.sephy.infra.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.fasterxml.jackson.annotation.JsonProperty;

import top.sephy.infra.auth.AuthenticationInfo;

public class CustomSecurityUser extends User implements AuthenticationInfo<Long> {

    public static final String KEY_USER_ID = "userId";

    public static final String KEY_NICKNAME = "nickname";

    private static final long serialVersionUID = 1007955580804484318L;

    private Map<String, Object> otherInfo = new HashMap<>();

    // @ConstructorProperties({"username", "password", "enabled", "accountNonExpired", "credentialsNonExpired",
    // "accountNonLocked", "authorities"})
    public CustomSecurityUser(@JsonProperty("username") String username, @JsonProperty("password") String password,
        @JsonProperty("enabled") boolean enabled, @JsonProperty("accountNonExpired") boolean accountNonExpired,
        @JsonProperty("credentialsNonExpired") boolean credentialsNonExpired,
        @JsonProperty("accountNonLocked") boolean accountNonLocked,
        @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities,
        @JsonProperty("otherInfo") Map<String, Object> otherInfo) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        if (otherInfo != null) {
            this.otherInfo.putAll(otherInfo);
        }
    }

    public Long getUserId() {
        return (Long)otherInfo.get(KEY_USER_ID);
    }

    public String getNickname() {
        return (String)otherInfo.get(KEY_NICKNAME);
    }

    public Map<String, Object> getOtherInfo() {
        return otherInfo == null || otherInfo.isEmpty() ? Collections.emptyMap()
            : Collections.unmodifiableMap(otherInfo);
    }
}
