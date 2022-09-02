package com.iabql.flashsale.mapper;

import com.iabql.flashsale.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2022-04-23
 */
@Mapper
public interface UserMapper {

    @Select({"select id,nickname,password,salt from t_user where id = #{id}"})
    @ResultType(com.iabql.flashsale.pojo.User.class)
    User selectById(String id);
}
