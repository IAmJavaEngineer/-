package com.wang.elema_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wang.elema_take_out.common.BaseContext;
import com.wang.elema_take_out.common.R;
import com.wang.elema_take_out.domain.ShoppingCart;
import com.wang.elema_take_out.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wjh
 * @create 2023-03-29
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 添加菜品/套餐到购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        //1、为购物车的用户id赋值
        shoppingCart.setUserId(BaseContext.getCurrentId());
        log.info("userId: {}",shoppingCart.getUserId());
        //2、判断当前加入购物车的是菜品还是套餐
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(shoppingCart.getUserId()!=null,ShoppingCart::getUserId,shoppingCart.getUserId());
        if(shoppingCart.getDishId()!=null){
            //加入购物车的是菜品
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            //加入购物车的是套餐
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //3、查询出当前购物车中的菜品/套餐
        ShoppingCart thisShoppingCart = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        //4、判断当前购物车里面是否已经有该菜品或套餐，如果有就更改数量，没有就加入到购物车，默认数量为1
        if (thisShoppingCart!=null){
            Integer number = thisShoppingCart.getNumber();
            thisShoppingCart.setNumber(number+1);
            shoppingCartService.updateById(thisShoppingCart);
        }else {
            thisShoppingCart = shoppingCart;
            thisShoppingCart.setNumber(1);
            shoppingCartService.save(thisShoppingCart);
        }
        return R.success(thisShoppingCart);
    }

    /**
     * 减少菜品/套餐数量，参数ShoppingCart中只有一个菜品/套餐id的值
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){

        shoppingCart.setUserId(BaseContext.getCurrentId());
        log.info("菜品/套餐：{}",shoppingCart);
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(shoppingCart.getUserId()!=null,ShoppingCart::getUserId,shoppingCart.getUserId());
        //1、判断是减少菜品数量还是减少套餐数量
        if (shoppingCart.getDishId()!=null){
            //减少的是菜品数量
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());

        } else {
            //减少的是套餐数量
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart thisShoppingCart = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        Integer number = thisShoppingCart.getNumber();
        //减少数量
        thisShoppingCart.setNumber(number-1);
        shoppingCartService.updateById(thisShoppingCart);
        //如果数量为0，直接删除该菜品/套餐
        if (thisShoppingCart.getNumber() == 0){
            shoppingCartService.removeById(thisShoppingCart);
        }
        return R.success(thisShoppingCart);

    }

    /**
     * 查询购物车列表
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        return R.success("清空购物车成功");
    }
}
