package com.iabql.flashsale.mapper;


import com.iabql.flashsale.pojo.Goods;
import com.iabql.flashsale.pojo.SeckillOrder;
import com.iabql.flashsale.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品表 Mapper 接口
 *
 * @author LiChao
 * @since 2022-03-03
 */
@Mapper
public interface GoodsMapper {

    @Select({"select g.id,g.goods_name,g.goods_title,g.goods_img,g.goods_detail,g.goods_price,g.goods_stock,\n" +
            "       sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date\n" +
            "from t_goods as g left join t_seckill_goods as sg on g.id=sg.goods_id"})
    @ResultType(com.iabql.flashsale.vo.GoodsVo.class)
    List<GoodsVo> findGoodsVo();


    @Select({"select g.id,g.goods_name,g.goods_title,g.goods_img,g.goods_detail,g.goods_price,g.goods_stock,\n" +
            "       sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date\n" +
            "from t_goods as g left join t_seckill_goods as sg on g.id=sg.goods_id  where g.id =#{goodsId};"})
    @ResultType(com.iabql.flashsale.vo.GoodsVo.class)
    GoodsVo findGoodsVoByGoodsId(Long goodsId);


    @Select({"select id,user_id,order_id,goods_id from t_seckill_order where user_id = #{userId} and goods_id = #{goodsId}"})
    @ResultType(com.iabql.flashsale.pojo.SeckillOrder.class)
    SeckillOrder getOne(Long userId, Long goodsId);


    /*@Update({"update t_seckill_goods set stock_count = #{stockCount} where goods_id = #{id}"})
    void updataSeckillGoods(GoodsVo goods);*/

    @Update({"update t_seckill_goods set stock_count = stock_count - 1 where stock_count > 0 and goods_id = #{id}"})
    void updataSeckillGoods(Long id);
}
