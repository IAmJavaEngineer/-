package com.wang.elema_take_out.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wjh
 * @create 2023-03-15
 */
@Data
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

//    关于那个ID相应给前端，雪花算法导致不一样的，可以在实体类上面添加注解
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;    //身份证号码

    private Integer status;

//    @JsonSerialize(contentUsing = LocalDateTimeSerializer.class)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
