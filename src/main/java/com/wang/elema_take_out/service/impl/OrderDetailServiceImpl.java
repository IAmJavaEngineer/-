package com.wang.elema_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.elema_take_out.domain.Dish;
import com.wang.elema_take_out.domain.DishFlavor;
import com.wang.elema_take_out.domain.OrderDetail;
import com.wang.elema_take_out.dto.DishDto;
import com.wang.elema_take_out.mapper.DishMapper;
import com.wang.elema_take_out.mapper.OrderDetailMapper;
import com.wang.elema_take_out.service.DishFlavorService;
import com.wang.elema_take_out.service.DishService;
import com.wang.elema_take_out.service.OrderDetailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wjh
 * @create 2023-03-15
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
