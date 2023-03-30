package com.wang.elema_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.elema_take_out.domain.OrderDetail;
import com.wang.elema_take_out.domain.Orders;

/**
 * @author wjh
 * @create 2023-03-15
 */
public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);
}
