package top.sephy.infra.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 线程上下文工具类, 方便获取用户信息, 一般场景请不要使用这个工具类
 */
@Slf4j
public abstract class ThreadContextUtils {

    private static final ThreadLocal<ThreadContext> INSTANCE = new InheritableThreadLocal<>();

    public static <E> E getUserId() {
        return (E)getContext().getUserId();
    }

    public static <E> void setUserId(E userId) {
        log.trace("set userId: {}", userId);
        getContext().setUserId(userId);
    }

    public static String getUsername() {
        return getContext().getUsername();
    }

    public static void setUsername(String username) {
        log.trace("set username: {}", username);
        getContext().setUsername(username);
    }

    private static ThreadContext getContext() {
        ThreadContext context = INSTANCE.get();
        if (context == null) {
            context = new ThreadContext();
            INSTANCE.set(context);
        }
        return context;
    }

    public static void clear() {
        INSTANCE.remove();
    }

    @Data
    private static class ThreadContext {

        private Object userId;

        private String username;
    }
}
