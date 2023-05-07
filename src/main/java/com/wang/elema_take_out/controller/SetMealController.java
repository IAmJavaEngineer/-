package com.wang.elema_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.elema_take_out.common.BaseContext;
import com.wang.elema_take_out.common.CustomException;
import com.wang.elema_take_out.common.R;
import com.wang.elema_take_out.domain.Category;
import com.wang.elema_take_out.domain.Dish;
import com.wang.elema_take_out.domain.Setmeal;
import com.wang.elema_take_out.domain.SetmealDish;
import com.wang.elema_take_out.dto.SetmealDto;
import com.wang.elema_take_out.service.CategoryService;
import com.wang.elema_take_out.service.SetmealDishService;
import com.wang.elema_take_out.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wjh
 * @create 2023-03-19
 */
@RestController
@Slf4j
@RequestMapping("/admin/setmeal")
@Transactional
@Api(tags = "套餐相关接口")
public class SetMealController {

    @Autowired
    SetmealService setmealService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    SetmealDishService setmealDishService;


    @PostMapping
    //@CacheEvict表示清理缓存，清理setmealCaChe分组下的所有缓存，allEntries默认为false，设置为true才能清理所有缓存
    @CacheEvict(value = "setmealCaChe",allEntries = true)
    @ApiOperation(value = "新增套餐接口")
    public R<String> insert(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }


    /**
     * 套餐管理页面展示分页数据
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "套餐分页接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true),
            @ApiImplicitParam(name = "page",value = "套餐名称",required = false)
    })
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name){
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);

        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(Strings.isNotEmpty(name),Setmeal::getName,name);
        //添加排序条件，根据更新时间降序排列
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,setmealLambdaQueryWrapper);
        /**
         * setmealPage的数据是有了，但是setmealPage中的数据是Setmeal类型的，只有分类idcategoryId，没有分类名
         * 所以如果直接返回setmealPage，页面是显示不出套餐分类名的
         * 这时候就需要用到对象拷贝了，将setmealPage的数据拷贝到setmealDtoPage，请注意，不要拷贝records，因为
         * records存放的是Setmeal类型，而setmealDtoPage的records存放的是SetmealDto类型
         * ignoreProperties就是忽略属性
         */
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        /**
         * 这样拷贝下来setmealDtoPage除了records属性，其他属性都有值了，那么怎么为records赋值呢
         * 我们可以先获取setmealPage中的records属性，它表示Setmeal集合
         * 通过Setmeal集合查到单条Setmeal中的idcategoryId，再根据idcategoryId从数据库查询套餐分类名
         * 赋值给setmealDtoPage中的SetmealDto集合中的SetmealDto对象，那么这也是只有套餐分类名啊，
         * 没有其他属性啊，这时就要又一次用到对象拷贝了，将setmealPage中的每条Setmeal属性拷贝到setmealDtoPage中的每条setmealDto
         */
        List<Setmeal> setmealList = setmealPage.getRecords();
        setmealDtoPage.setRecords(
                setmealList.stream().map((item)->{
                //将setmealList中每个Setmeal通过属性拷贝到新的setmealDto
                SetmealDto setmealDto = new SetmealDto();
                BeanUtils.copyProperties(item,setmealDto);
                //获取套餐分类id
                Long categoryId = item.getCategoryId();
                //根据id查询分类名
                LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
                categoryLambdaQueryWrapper.eq(Category::getType,2);
                Category category = categoryService.getById(categoryId);
                //获得套餐分类名，将分类名赋值给setmealDto
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
                return setmealDto;
            }).collect(Collectors.toList())
        );
        return R.success(setmealDtoPage);
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCaChe",allEntries = true)
    @ApiOperation(value = "删除套餐接口")
    @ApiImplicitParam(name = "ids",value = "id数组",required = true)
    //前端传过来的参数是数组，不是字面量了，需要用@RequestParam来指定参数接收 ids=1,2,3
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除成功！");
    }

    /**
     * 起售和停售操作，包括起售/停售一个和批量起售/停售
     * @param status 表示地址中的一个路径，如果是0，就表示此操作为停售，如果是1，则表示此操作为起售
     * @param ids
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCaChe",allEntries = true)
    @ApiOperation(value = "套餐起售和停售接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status",value = "状态",required = true),
            @ApiImplicitParam(name = "ids",value = "id数组",required = true)
    })
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids){
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
        Setmeal setmeal = new Setmeal();
        if(status == 0){
            //停售操作
            //条件是id在ids里，并且状态为1
            setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);
            setmeal.setStatus(0);
        }else{
            //起售操作
            //条件是id在ids里，并且状态为0
            setmealLambdaQueryWrapper.eq(Setmeal::getStatus,0);
            setmeal.setStatus(1);
        }
        //修改操作，只会修改status为0，dish中为null的属性不会将数据库中的对应字段替换
        setmealService.update(setmeal,setmealLambdaQueryWrapper);
        return R.success("停售成功！");
    }

    @GetMapping("/list")
    //注意这里的双引号和单引号的用法
    @Cacheable(value = "setmealCaChe", key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealService.list(setmealLambdaQueryWrapper);
        return R.success(setmeals);
    }

    /**
     * 修改套餐第一步：回显套餐数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> SetmealEcho(@PathVariable String id){
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        //为Dto的分类名赋值
        Category category = categoryService.getById(setmealDto.getCategoryId());
        setmealDto.setCategoryName(category.getName());
        //查询这个套餐id下的所有菜品，为Dto的菜品集合赋值
        LambdaQueryWrapper<SetmealDish> setmealDtoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDtoLambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(setmealDtoLambdaQueryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        return R.success(setmealDto);
    }


    /**
     * 修改套餐第二步，修改同时清除缓存
     * @param setmealDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "setmealCaChe",allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto){
        //将套餐表的数据修改
        setmealService.updateById(setmealDto);

        /**
         * 还有套餐下的菜品集合也要修改，修改的表时setmeal_dish
         * 修改的步骤为
         *      1、在setmeal_dish表中删除这个套餐下的所有菜品，根据套餐id删除
         *      2、重新添加这个套餐下的菜品集合
         */
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((temp)->{
            temp.setSetmealId(setmealDto.getId());
            return temp;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
        return R.success("修改成功");
    }
}
