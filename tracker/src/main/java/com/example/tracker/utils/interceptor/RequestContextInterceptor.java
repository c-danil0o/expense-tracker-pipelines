package com.example.tracker.utils.interceptor;

import com.example.tracker.utils.AccountLogic;
import com.example.tracker.utils.UserClientContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestContextInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AccountLogic accountLogic = new AccountLogic();
        UserClientContext.setCurrentContext(accountLogic.getClientInfo(request));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserClientContext.clear();
    }

}
