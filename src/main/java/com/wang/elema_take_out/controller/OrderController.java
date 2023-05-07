package com.wang.elema_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.elema_take_out.common.BaseContext;
import com.wang.elema_take_out.common.R;
import com.wang.elema_take_out.domain.OrderDetail;
import com.wang.elema_take_out.domain.OrderStatus;
import com.wang.elema_take_out.domain.Orders;
import com.wang.elema_take_out.domain.User;
import com.wang.elema_take_out.dto.OrdersDto;
import com.wang.elema_take_out.service.OrderDetailService;
import com.wang.elema_take_out.service.OrdersService;
import com.wang.elema_take_out.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wjh
 * @create 2023-03-29
 */
@RestController
@Slf4j
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

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
        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
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

    /**
     * 后台订单分页显示
     * SELECT id,number,status,user_id,address_book_id,order_time,checkout_time,pay_method,amount,remark,user_name,phone,address,consignee FROM orders
     * WHERE order_time >= '2023-06-29 00:00:00' AND order_time <= '2023-06-30 23:59:59'
     * LIMIT 10
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     * @throws ParseException
     */
    @GetMapping("/page")
    public R<Page<OrdersDto>> ordersPage(Integer page,Integer pageSize,Long number,String beginTime,String endTime) throws ParseException {
        log.info("{}",beginTime);
        log.info("{}",endTime);
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(number != null,Orders::getNumber,number).orderByDesc(Orders::getOrderTime);
        if (Strings.isNotEmpty(beginTime) && Strings.isNotEmpty(endTime)){
            ordersLambdaQueryWrapper.ge(Orders::getOrderTime,beginTime).le(Orders::getOrderTime,endTime);
        }
        ordersService.page(ordersPage,ordersLambdaQueryWrapper);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        List<Orders> ordersList = ordersPage.getRecords();
        ordersDtoPage.setRecords(ordersList.stream().map((order)->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(order,ordersDto);
            User user = userService.getById(order.getUserId());
            ordersDto.setUserName(user.getName());
            return ordersDto;
        }).collect(Collectors.toList()));

        return R.success(ordersDtoPage);
    }

    /**
     * 修改订单状态，改为3，代表已哌送
     */
    @PutMapping
    public R<String> pipeLined(@RequestBody OrderStatus orderStatus){
        Integer status = orderStatus.getStatus();
        Long id = orderStatus.getId();
        log.info("status:{},id:{}",status,id);
        LambdaUpdateWrapper<Orders> ordersLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        ordersLambdaUpdateWrapper.set(status != null,Orders::getStatus,status).eq(id != null,Orders::getId,id);
        ordersService.update(ordersLambdaUpdateWrapper);
        return R.success("订单状态修改成功");

    }
}
