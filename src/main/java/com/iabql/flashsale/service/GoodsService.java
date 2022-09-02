package com.iabql.flashsale.service;

import com.iabql.flashsale.vo.GoodsVo;

import java.util.List;
public interface GoodsService {
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
