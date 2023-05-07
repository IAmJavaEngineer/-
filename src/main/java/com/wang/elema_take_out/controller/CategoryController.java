package com.wang.elema_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.elema_take_out.common.R;
import com.wang.elema_take_out.domain.Category;
import com.wang.elema_take_out.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wjh
 * @create 2023-03-17
 */
@RestController
@Slf4j
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @PostMapping
    /**
     * 添加分类
     */
    public R<String> save(@RequestBody Category category){
        System.out.println(category);
//        log.info("分类信息：{}",category.toString());
        categoryService.save(category);
        return R.success("添加分类成功！");
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize){

        //创建分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据Sort字段升序
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除分类
     */
    @DeleteMapping
    public R<String> delete(Long ids){
//        调用自定义删除方法
        categoryService.remover(ids);
        return R.success("");
    }

    /**
     * 修改分类
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("要修改分类的id：{}",category.getId());
        categoryService.updateById(category);
        return R.success("修改成功！");
    }



    /**
     * 查询菜品分类or套餐分类，在前端下拉框显示
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getCreateTime);
        List<Category> categoryList = categoryService.list(lambdaQueryWrapper);
        return R.success(categoryList);
    }
}
