package com.wang.elema_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.elema_take_out.common.R;
import com.wang.elema_take_out.domain.Category;
import com.wang.elema_take_out.domain.Dish;
import com.wang.elema_take_out.domain.DishFlavor;
import com.wang.elema_take_out.dto.DishDto;
import com.wang.elema_take_out.service.CategoryService;
import com.wang.elema_take_out.service.DishFlavorService;
import com.wang.elema_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wjh
 * @create 2023-03-18
 */
@RestController
@RequestMapping("/dish")
@Slf4j
@Transactional      //开启事务
public class DishController {

    @Autowired
    DishService dishService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate redisTemplate;


    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
//
//        System.out.println(dishDto);
//        System.out.println(dishDto.getId());
        dishService.saveWithFlavor(dishDto);
        return R.success("保存成功！");
    }

    @GetMapping("/page")
    //name参数是菜品管理中搜索才会添加的参数
    public R<Page> page(Integer page, Integer pageSize, String name){

        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(Strings.isNotEmpty(name),Dish::getName,name);
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,dishLambdaQueryWrapper);
        //查询出来的dishPage中的Dish类中只有categoryId菜品分类的Id，而前端想要的是菜品分类的名称，所以不能返回dishPage
        //使用对象拷贝，返回Page<DishDto>DishDto的分页，因为DishDto有菜品分类的名称
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        List<Dish> records = dishPage.getRecords();
        List <DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId(); //分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }


    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        return R.success(dishService.getWithFlavor(id));
    }

    /**
     * 每次修改数据都要清理缓存
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
//      清理所有缓存
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);

//        根据修改某个菜品，清理这个菜品所在的分类id的缓存
//        构造key
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("修改成功");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dishLambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        dishLambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getStatus,1);
//        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
//        List<Dish> dishes = dishService.list(dishLambdaQueryWrapper);
//        return R.success(dishes);
//    }

    /**
     * 在移动端显示规格，即菜品的口味信息，所以需要使用到DishDto，查询菜品的同时带上规格信息
     * @param dishDto 这个dishDto里面只有一个分类id
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(DishDto dishDto){
        //构造一个key,大概长这样：dish_1397844303408574465_1
        String key = "dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        List<DishDto> dishDtos = null;
        //先从redis数据库/缓存中查询数据，如果有缓存中有数据，直接返回，就不用查询MySQL数据库了
        dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtos != null){
            return R.success(dishDtos);
        }
        //如果缓存中没有数据，就去查询MySQL数据库
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dishDto.getCategoryId()!=null,Dish::getCategoryId,dishDto.getCategoryId());
        lambdaQueryWrapper.eq(dishDto.getCategoryId()!=null,Dish::getStatus,1);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishService.list(lambdaQueryWrapper);
        dishDtos = dishes.stream().map((item) ->{
            DishDto dishDto1 = new DishDto();
            BeanUtils.copyProperties(item,dishDto1);
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(dishDto1.getId() != null,DishFlavor::getDishId,dishDto1.getId());
//            SQL：select * from dish_flavors where dish_id = ?
            List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto1.setFlavors(flavors);
            return dishDto1;
        }).collect(Collectors.toList());
        //查完MySQL数据库，要把查询的数据在redis中存放一份，以便下次查询时直接查询缓存
        redisTemplate.opsForValue().set(key,dishDtos,60l, TimeUnit.MINUTES);
        dishDtos.forEach(System.out::println);
        return R.success(dishDtos);

    }

    /**
     * 起售和停售操作，包括起售/停售一个和批量起售/停售
     * @param status 表示地址中的一个路径，如果是0，就表示此操作为停售，如果是1，则表示此操作为起售
     * @param ids
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId,ids);
        Dish dish = new Dish();
        if(status == 0){
            //条件是id在ids里，并且状态为1
            dishLambdaQueryWrapper.eq(Dish::getStatus,1);
            dish.setStatus(0);
        }else{
            //条件是id在ids里，并且状态为0
            dishLambdaQueryWrapper.eq(Dish::getStatus,0);
            dish.setStatus(1);
        }
        //修改操作，只会修改status为0，dish中为null的属性不会将数据库中的对应字段替换
        dishService.update(dish,dishLambdaQueryWrapper);
        return R.success("停售成功！");
    }



}
