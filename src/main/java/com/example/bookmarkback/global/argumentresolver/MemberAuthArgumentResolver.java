package com.example.bookmarkback.global.argumentresolver;


import com.example.bookmarkback.global.dto.MemberAuth;
import com.example.bookmarkback.global.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class MemberAuthArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType()
                .equals(MemberAuth.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        if (httpServletRequest.getAttribute("member_id") == null) {
            throw new UnauthorizedException("인증되지 않은 사용자입니다.");
        }
        String memberId = httpServletRequest.getAttribute("member_id").toString();

        return new MemberAuth(Long.valueOf(memberId));
    }
}

