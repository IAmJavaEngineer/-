package com.wang.elema_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.elema_take_out.common.BaseContext;
import com.wang.elema_take_out.common.CustomException;
import com.wang.elema_take_out.domain.Category;
import com.wang.elema_take_out.domain.Dish;
import com.wang.elema_take_out.domain.Employee;
import com.wang.elema_take_out.domain.Setmeal;
import com.wang.elema_take_out.mapper.CategoryMapper;
import com.wang.elema_take_out.mapper.EmployeeMapper;
import com.wang.elema_take_out.service.CategoryService;
import com.wang.elema_take_out.service.DishService;
import com.wang.elema_take_out.service.EmployeeService;
import com.wang.elema_take_out.service.SetmealService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wjh
 * @create 2023-03-15
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;
    @Override
    public void remover(Long ids){

        //查询当前分类下是否又菜品，如果有，抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int count = dishService.count(dishLambdaQueryWrapper);
        if (count > 0){
            throw new CustomException("当前分类下存在菜品，不能删除！");
        }


        //查询当前分类下是否有套餐，如果有，抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0){
            throw new CustomException("当前分类下存在套餐，不能删除！");
        }

        //正常删除，调用父接口的removeById方法
        super.removeById(ids);
    }

}
