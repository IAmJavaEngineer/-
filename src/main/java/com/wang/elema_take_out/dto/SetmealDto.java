package com.wang.elema_take_out.dto;

import com.wang.elema_take_out.domain.Setmeal;
import com.wang.elema_take_out.domain.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    //套餐下面的菜品集合
    private List<SetmealDish> setmealDishes;

    //套餐分类名
    private String categoryName;
}
