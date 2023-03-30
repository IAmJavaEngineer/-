package com.wang.elema_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.elema_take_out.common.CustomException;
import com.wang.elema_take_out.common.R;
import com.wang.elema_take_out.domain.Dish;
import com.wang.elema_take_out.domain.Setmeal;
import com.wang.elema_take_out.domain.SetmealDish;
import com.wang.elema_take_out.dto.SetmealDto;
import com.wang.elema_take_out.mapper.DishMapper;
import com.wang.elema_take_out.mapper.SetmealMapper;
import com.wang.elema_take_out.service.DishService;
import com.wang.elema_take_out.service.SetmealDishService;
import com.wang.elema_take_out.service.SetmealService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wjh
 * @create 2023-03-15
 */
@Service
@Transactional
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Autowired
    SetmealDishService setmealDishService;



    /**
     * 添加套餐信息，以及该套餐对应下的菜品集合
     * 要操作两张表，添加事务注解
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //先保存setmeal表的信息
        this.save(setmealDto);
        //SetmealDish这是套餐对应下的菜品集合（简称套餐菜品），但是这些套餐菜品信息没有对应的setmeal_id，我们要将
        //这些套餐菜品对应的套餐id给一一赋值上，那么这些套餐id从哪里来的呢，当然时从setmealDto获取的
        //但是前台都没有传套餐id，只传categoryId，而这是套餐分类id，我们想要的时套餐id
        //因为套餐id时我们保存套餐时才会生成的，就是我们执行this.save(setmealDto);这个方法，保存的同时获取主键为setmealDto赋值
        System.out.println(setmealDto.getId());
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //此时setmealDto就有id了，List<SetmealDish>也有对应的套餐id了
        //就对setmeal_dish表进行多条数据插入操作就可以了
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void removeWithDish(List<Long> ids) {
//        //select count(*) from setmeal where id in (1,2,3) and status = 1
//        //先来查询套餐是否是停售，只有停售的套餐才可以被删除
//        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
//        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);
//        int count = this.count(setmealLambdaQueryWrapper);
//        if (count > 0) {
//            throw new CustomException("删除失败,当前套餐正在启售，请先停售再来删除吧");
//        }
//        //删除setmeal套餐表的信息，还要删除套餐对应下的菜品信息
//        this.removeByIds(ids);
//        //delete from setmeal_dish where setmeal_id in (1,2,3)
//        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
//        setmealDishService.remove(setmealDishLambdaQueryWrapper);


        //select count(*) from setmeal where id in (1,2,3) and status = 1
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if(count > 0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐表中的数据---setmeal
        this.removeByIds(ids);

        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //删除关系表中的数据----setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);
    }


}
