package com.iabql.flashsale.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author jobob
 * @since 2022-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long id;

    private String nickname;

    private String password;

    /**
     * md5(明文+固定salt)+随机salt
     */
    private String salt;

    private String head;

    private Date registerDate;

    /**
     * 最后一次登入时间
     */
    private Date lastLoginDate;

    /**
     * 登入次数
     */
    private Integer loginCount;


}
