package top.sephy.infra.auth;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationInfoResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(AuthenticationInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication annotation = parameter.getParameterAnnotation(Authentication.class);

        org.springframework.security.core.Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        Object value = null;
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof AuthenticationInfo) {
                value = principal;
            }
        }

        if (value == null) {
            if (annotation == null || annotation.required()) {
                throw new ServletRequestBindingException("用户身份认证信息缺失");
            }
        }

        return value;
    }
}
