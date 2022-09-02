package com.iabql.flashsale.mapper;


import com.iabql.flashsale.pojo.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
 *  Mapper 接口
 *
 * @author LiChao
 * @since 2022-03-03
 */
@Mapper
public interface OrderMapper {

    @Insert({"insert into t_order (user_id, goods_id, delivery_addr_id, goods_name, goods_count, goods_price, order_channel, status, create_date, pay_date) \n" +
            "values (#{userId},#{goodsId},#{deliveryAddrId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate},#{payDate})"})
    @Options(keyProperty = "id",keyColumn = "id",useGeneratedKeys = true)
    void save(Order order);
}
