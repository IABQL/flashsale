package com.iabql.flashsale.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iabql.flashsale.mapper.UserMapper;
import com.iabql.flashsale.pojo.User;
import com.iabql.flashsale.service.UserService;
import com.iabql.flashsale.utils.CookieUtil;
import com.iabql.flashsale.utils.MD5Util;
import com.iabql.flashsale.utils.UUIDUtil;
import com.iabql.flashsale.utils.ValidatorUtil;
import com.iabql.flashsale.vo.LoginVo;
import com.iabql.flashsale.vo.RespBean;
import com.iabql.flashsale.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2022-04-23
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        if (StringUtils.isEmpty((mobile)) || StringUtils.isEmpty(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if (!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }
        //查询用户
        User user = userMapper.selectById(mobile);
        if (user == null){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        if (!MD5Util.fromPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        //生成cookie
        String ticket = UUIDUtil.uuid();
        //request.getSession().setAttribute(ticket,user);
        addLoginTicket(ticket, JSON.toJSONString(user));
        CookieUtil.setCookie(request,response,"userTicket",ticket);
        return RespBean.success(ticket);
    }

    /*
     * 向redis存储登入凭证
     * key：ticket      value:user   expired:过期时间 /秒
     */
    public void addLoginTicket(String key,String value){
        //向redis写入登入信息,key:ticket,value:user,过期时间：秒
        redisTemplate.opsForValue().set("ticket_"+key, value);
    }

    //登入凭证查询（redis）
    public User findLoginTicket(String ticket){
        //取出json字符串，将字符串转为user对象
        return JSONObject.parseObject(redisTemplate.opsForValue().get("ticket_" + ticket),User.class);
    }
}
