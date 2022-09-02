package com.iabql.flashsale.service;

import com.iabql.flashsale.pojo.User;
import com.iabql.flashsale.vo.LoginVo;
import com.iabql.flashsale.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2022-04-23
 */
public interface UserService {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
}
