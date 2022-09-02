package com.iabql.flashsale.controller;

import com.iabql.flashsale.pojo.Order;
import com.iabql.flashsale.pojo.SeckillOrder;
import com.iabql.flashsale.pojo.User;
import com.iabql.flashsale.service.GoodsService;
import com.iabql.flashsale.service.impl.OrderServiceImpl;
import com.iabql.flashsale.service.impl.SeckillOrderServiceImpl;
import com.iabql.flashsale.service.impl.UserServiceImpl;
import com.iabql.flashsale.vo.GoodsVo;
import com.iabql.flashsale.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.util.StringUtils;

import java.util.List;



@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SeckillOrderServiceImpl seckillOrderService;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    /**
     * 数据库qps：323
     * qps:2469
     *
     * 利⽤redis的单线程预减库存。⽐如商品有100件。那么我在Redis存储⼀个K，V。例如 <gs1001, 100>，每⼀个⽤户线程进来，key
     * 值就减1，等减到0的时候，全部拒绝剩下的请求。那么也就是只有100个线程会进⼊到后续操作。所以⼀定不会出现超卖的现象
     */
    @RequestMapping("/doSeckill")
    public String doSeckill(Model model, Long goodsId, @CookieValue("userTicket") String ticket){



        if (StringUtils.isEmpty(ticket)){
            return "/login";
        }

        User user = userService.findLoginTicket(ticket);
        if (user == null){
            return "/login";
        }
        model.addAttribute("user",user);

        ValueOperations valueOperations = redisTemplate.opsForValue();

        //数据库查询，判断是否重复抢购
        //SeckillOrder one = seckillOrderService.getOne(user.getId(), goodsId);


        //改进，不再查询数据库是否重复购买，向redis查询，提高效率
        //(利用商品id+用户id生成对当前用户有效的锁)如果锁存在则无法设置成功，防止用户多次点击，在redis判断订单时都为不存在且库存足够时，造成重复购买
        Boolean lock = valueOperations.setIfAbsent("stock_lock:" + goodsId+user.getId(), goodsId.toString());

        if (lock){
            try {
                String one = orderService.findSeckillOrder(String.valueOf(user.getId()));
                if (one != null && one.equals(String.valueOf(goodsId))){
                    model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
                    return "/secKillFail";
                }

                //redis预减库存
                Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
                if (stock<0){
                    //库存不足
                    model.addAttribute("errmsg",RespBeanEnum.EMPTY_STOCK.getMessage());
                    return "/secKillFail";
                }


                //生成订单表
                GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
                Order order = orderService.seckill(user,goods);
                model.addAttribute("order",order);
                model.addAttribute("goods",goods);
            }finally {
                //删除锁
                redisTemplate.delete("stock_lock:" + goodsId+user.getId());
            }

        }


        return "/orderDetail";

        /*
        旧版，向mysql查询
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if(goods.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "/secKillFail";//跳转秒杀失败页面
        }
        //用户是否已经购买过,限购一件
        SeckillOrder one = seckillOrderService.getOne(user.getId(), goodsId);
        if (one != null){
            model.addAttribute("errmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return "/secKillFail";
        }

        Order order = orderService.seckill(user,goods);
        model.addAttribute("order",order);
        model.addAttribute("goods",goods);
        //跳转订单详情
        return "/orderDetail";

         */


    }


    /**
     * 初始化，将秒杀商品加载到redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (list.isEmpty()){
            return;
        }
        //存入redis
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount().toString());
        });
    }
}
