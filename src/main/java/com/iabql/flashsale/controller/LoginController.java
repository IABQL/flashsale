package com.iabql.flashsale.controller;

import com.iabql.flashsale.service.UserService;
import com.iabql.flashsale.vo.LoginVo;
import com.iabql.flashsale.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;

    //跳转登入页面
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "/login";
    }


    //登入
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        return userService.doLogin(loginVo,request,response);
    }
}
