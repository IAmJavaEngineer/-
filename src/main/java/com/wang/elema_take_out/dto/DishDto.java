package com.wang.elema_take_out.dto;

import com.wang.elema_take_out.domain.Dish;
import com.wang.elema_take_out.domain.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wjh
 * @create 2023-03-18
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
