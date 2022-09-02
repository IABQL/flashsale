package com.iabql.flashsale.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.iabql.flashsale.mapper.OrderMapper;
import com.iabql.flashsale.mapper.SeckillOrderMapper;
import com.iabql.flashsale.pojo.Order;
import com.iabql.flashsale.pojo.SeckillOrder;
import com.iabql.flashsale.pojo.User;
import com.iabql.flashsale.service.GoodsService;
import com.iabql.flashsale.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 服务实现类
 *
 * @author LiChao
 * @since 2022-03-03
 */
@Service
@Primary
public class OrderServiceImpl {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SeckillGoodsServiceImpl seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    public Order seckill(User user, GoodsVo goods) {
        //秒杀商品表减库存
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goods.getId());
        //goodsVo.setStockCount(goodsVo.getStockCount()-1);
        seckillGoodsService.updataSeckillGoods(goods.getId());

        //生成订单
        Order order = new Order();
        order.setUserId(user.getId())
            .setGoodsId(goodsVo.getId())
            .setDeliveryAddrId(0L)
            .setGoodsName(goodsVo.getGoodsName())
            .setGoodsCount(1)
            .setGoodsPrice(goodsVo.getSeckillPrice())
            .setOrderChannel(1)
            .setStatus(0)
            .setCreateDate(new Date());

        //订单存入mysql
        orderMapper.save(order);

        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goodsVo.getId());

        //秒杀订单存入mysql
        seckillOrderMapper.save(seckillOrder);

        //秒杀订单存入redis
        addSeckillOrder(String.valueOf(user.getId()), String.valueOf(goodsVo.getId()));
        return order;
    }

    //redis存入订单信息
    public void addSeckillOrder(String userId,String goodsId){
        redisTemplate.opsForValue().set("seckillOrder_"+ userId, goodsId);
    }

    public String findSeckillOrder(String userId) {
        Object o = redisTemplate.opsForValue().get("seckillOrder_" + userId);
        if (o != null){
            return o.toString();
        }
        return null;
    }
}
