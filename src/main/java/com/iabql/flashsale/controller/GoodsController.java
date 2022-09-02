package com.iabql.flashsale.controller;

import com.iabql.flashsale.annotation.LoginRequired;
import com.iabql.flashsale.pojo.User;
import com.iabql.flashsale.service.GoodsService;
import com.iabql.flashsale.service.impl.UserServiceImpl;
import com.iabql.flashsale.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.Date;


@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserServiceImpl userService;


    @RequestMapping("/toList")
    @LoginRequired
    public String toList(Model model,@CookieValue("userTicket") String ticket){


        if (StringUtils.isEmpty(ticket)){
            return "/login";
        }
        User user = userService.findLoginTicket(ticket);

        if (user == null){
            return "/login";
        }
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());
        return "/goodsList";
    }

    @RequestMapping("/toDetail/{goodsId}")
    @LoginRequired
    public String toDetail(Model model, @CookieValue("userTicket") String ticket,
                           @PathVariable Long goodsId){
        if (StringUtils.isEmpty(ticket)){
            return "/login";
        }
        User user = userService.findLoginTicket(ticket);
        if (user == null){
            return "/login";
        }


        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        model.addAttribute("user",user);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();

        int remainSeconds = 0;//秒杀倒计时
        int secKillStatus = 0;//秒杀状态
        //秒杀未开始
        if (nowDate.before(startDate)){
            secKillStatus = 0;
            remainSeconds = (int) ((startDate.getTime()-nowDate.getTime())/1000);
        }else if(nowDate.after(endDate)){
            //秒杀结束
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            //秒杀进行中
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("goods",goods);
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        return "/goodsDetail";
    }
}
