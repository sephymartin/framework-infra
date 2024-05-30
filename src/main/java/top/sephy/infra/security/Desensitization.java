package top.sephy.infra.security;

public interface Desensitization {

    /**
     * 脱敏实现
     *
     * @param target 脱敏对象
     * @return 脱敏返回结果
     */
    String desensitize(String target);
}
