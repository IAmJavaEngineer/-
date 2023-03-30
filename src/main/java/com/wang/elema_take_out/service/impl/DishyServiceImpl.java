package com.wang.elema_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.elema_take_out.common.R;
import com.wang.elema_take_out.domain.Category;
import com.wang.elema_take_out.domain.Dish;
import com.wang.elema_take_out.domain.DishFlavor;
import com.wang.elema_take_out.domain.Setmeal;
import com.wang.elema_take_out.dto.DishDto;
import com.wang.elema_take_out.mapper.CategoryMapper;
import com.wang.elema_take_out.mapper.DishMapper;
import com.wang.elema_take_out.service.CategoryService;
import com.wang.elema_take_out.service.DishFlavorService;
import com.wang.elema_take_out.service.DishService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wjh
 * @create 2023-03-15
 */
@Service
public class DishyServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    DishService dishService;
    /**
     * 新增菜品，并插入口味数据，因此要操作两张表
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品
        this.save(dishDto);
        //获取保存菜品的id
        Long DishId = dishDto.getId();
        //为菜品口味的id赋值
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> flavorList = new ArrayList<>();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(DishId);
            flavorList.add(flavor);
        }
        //保存菜品口味数据，这个数据是个List集合，用saveBatch进行保存，注意这不是批量插入，是有多少条数据，就执行多少条
        //INSERT INTO dish_flavor ( id, dish_id, name, value, create_time, update_time, create_user, update_user ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )
        dishFlavorService.saveBatch(flavorList);
    }

    /**
     * 修改时回显菜品信息，因为dish表只有口味id，所以还有在口味表里查询口味名，要查两张表
     * @param id
     */
    @Override
    public DishDto getWithFlavor(Long id) {
        //查询菜品信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        //对象拷贝，将dish的信息拷贝到dishDto
        BeanUtils.copyProperties(dish,dishDto);
        //查询口味信息
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(id != null,DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(list);
        return dishDto;

    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表
        this.updateById(dishDto);
        //删除口味表对应的信息
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        //插入口味信息到口味表
        List<DishFlavor> flavors = dishDto.getFlavors();
        //flavors只有口味信息，没有对应的菜品id
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }




}
