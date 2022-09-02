package com.iabql.flashsale.service.impl;

import com.iabql.flashsale.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * 秒杀商品表 服务实现类
 *
 * @author LiChao
 * @since 2022-03-03
 */
@Service
@Primary
public class SeckillGoodsServiceImpl {

    @Autowired
    private GoodsMapper goodsMapper;

    /*public void updataSeckillGoods(GoodsVo goods) {
        goodsMapper.updataSeckillGoods(goods);
    }*/

    public void updataSeckillGoods(Long goodsId) {
        goodsMapper.updataSeckillGoods(goodsId);
    }
}
