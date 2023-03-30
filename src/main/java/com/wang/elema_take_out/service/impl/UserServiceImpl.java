package com.wang.elema_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.elema_take_out.common.CustomException;
import com.wang.elema_take_out.domain.Setmeal;
import com.wang.elema_take_out.domain.SetmealDish;
import com.wang.elema_take_out.domain.User;
import com.wang.elema_take_out.dto.SetmealDto;
import com.wang.elema_take_out.mapper.SetmealMapper;
import com.wang.elema_take_out.mapper.UserMapper;
import com.wang.elema_take_out.service.SetmealDishService;
import com.wang.elema_take_out.service.SetmealService;
import com.wang.elema_take_out.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wjh
 * @create 2023-03-15
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
