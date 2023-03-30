package com.wang.elema_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.elema_take_out.domain.Category;
import com.wang.elema_take_out.domain.Dish;
import com.wang.elema_take_out.dto.DishDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author wjh
 * @create 2023-03-15
 */
public interface DishService extends IService<Dish> {

    public void saveWithFlavor(DishDto dishDto);

    public DishDto getWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

}
