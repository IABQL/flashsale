package com.iabql.flashsale.mapper;


import com.iabql.flashsale.pojo.SeckillOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀订单表 Mapper 接口
 *
 * @author LiChao
 * @since 2022-03-03
 */
@Mapper
public interface SeckillOrderMapper {

    @Insert({"insert into t_seckill_order ( user_id, order_id, goods_id) values" +
            " (#{userId},#{orderId},#{goodsId})"})
    void save(SeckillOrder seckillOrder);
}
