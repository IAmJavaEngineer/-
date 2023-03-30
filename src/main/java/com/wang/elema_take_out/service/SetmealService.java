package com.wang.elema_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.elema_take_out.domain.Dish;
import com.wang.elema_take_out.domain.Setmeal;
import com.wang.elema_take_out.dto.DishDto;
import com.wang.elema_take_out.dto.SetmealDto;

import java.util.List;

/**
 * @author wjh
 * @create 2023-03-15
 */
public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}
