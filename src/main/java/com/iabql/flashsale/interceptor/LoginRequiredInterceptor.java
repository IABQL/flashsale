package com.iabql.flashsale.interceptor;

import com.iabql.flashsale.annotation.LoginRequired;
import com.iabql.flashsale.pojo.User;
import com.iabql.flashsale.service.impl.UserServiceImpl;
import com.iabql.flashsale.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

/**
 * 拦截注解LoginRequired的方法，该方法必须要登入才能访问
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private UserServiceImpl userService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果访问的是controller请求
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;//强转
            Method method = handlerMethod.getMethod();//获取方法
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);//获取方法是否含有注解LoginRequired
            //如果加了该注解，表示必须要登入才能访问

            String ticket = CookieUtil.getCookieValue(request,"userTicket");
            User user = userService.findLoginTicket(ticket);

            if (loginRequired != null && user == null){
                response.sendRedirect(request.getContextPath()+"/login/toLogin");//重定向到登入页面
                return false;//未登入拒绝
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
