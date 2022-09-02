package com.iabql.flashsale.service.impl;

import com.iabql.flashsale.mapper.GoodsMapper;
import com.iabql.flashsale.pojo.SeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


/**
 * 秒杀订单表 服务实现类
 *
 * @author LiChao
 * @since 2022-03-03
 */
@Service
@Primary
public class SeckillOrderServiceImpl {

    @Autowired
    private GoodsMapper goodsMapper;

    public SeckillOrder getOne(Long userId, Long goodsId) {
        return goodsMapper.getOne(userId,goodsId);
    }
}
