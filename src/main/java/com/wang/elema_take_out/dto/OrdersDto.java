package com.wang.elema_take_out.dto;

import com.wang.elema_take_out.domain.Dish;
import com.wang.elema_take_out.domain.DishFlavor;
import com.wang.elema_take_out.domain.Orders;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wjh
 * @create 2023-03-18
 */
@Data
public class OrdersDto extends Orders {
    //这条订单下一共有多少商品
    private Integer sumNum;

    //这条订单的用户
    private String userName;

}
