package com.wang.elema_take_out.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wang.elema_take_out.domain.Category;
import com.wang.elema_take_out.domain.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wjh
 * @create 2023-03-15
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
