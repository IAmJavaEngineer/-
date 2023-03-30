package com.wang.elema_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.elema_take_out.common.BaseContext;
import com.wang.elema_take_out.common.R;
import com.wang.elema_take_out.domain.OrderDetail;
import com.wang.elema_take_out.domain.Orders;
import com.wang.elema_take_out.dto.OrdersDto;
import com.wang.elema_take_out.service.OrderDetailService;
import com.wang.elema_take_out.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wjh
 * @create 2023-03-29
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 订单列表分页功能
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> page(Integer page, Integer pageSize){
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(userId != null, Orders::getUserId, userId);
        Page<Orders> page1 = ordersService.page(ordersPage, ordersLambdaQueryWrapper);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(page1,ordersDtoPage,"records");

        List<Orders> records = page1.getRecords();
        ordersDtoPage.setRecords(records.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            //查询这条订单下有多少件商品，先查询出这条订单的id，根据订单id在订单order_detail表中查出数量
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,item.getId());
            Integer count = orderDetailService.count(orderDetailLambdaQueryWrapper);
            ordersDto.setSumNum(count);
            return ordersDto;
        }).collect(Collectors.toList()));
        return R.success(ordersDtoPage);
    }
}
