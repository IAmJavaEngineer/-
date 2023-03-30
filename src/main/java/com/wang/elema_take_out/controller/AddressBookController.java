package com.wang.elema_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wang.elema_take_out.common.BaseContext;
import com.wang.elema_take_out.common.R;
import com.wang.elema_take_out.domain.AddressBook;
import com.wang.elema_take_out.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author wjh
 * @create 2023-03-28
 */
@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 添加地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> saveAddressBook(@RequestBody AddressBook addressBook){
        log.info(addressBook.toString());
        //为这个地址的用户id赋值，从线程中获取用户id
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("添加地址成功");
    }

    /**
     * 显示地址集合
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> listAddressBook(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        //只显示当前用户下的地址管理信息
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 设置默认地址，默认地址只有一个，0表示不是默认，1表示默认，添加地址时，数据库中的is_default默认是0
     * @param
     * @return
     */
    @PutMapping("/default")
    public R<String> defaultAddress(@RequestBody AddressBook addressBook){
        //1、先将当前用户中所有的地址设置为0
        LambdaUpdateWrapper<AddressBook> addressBookLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        addressBookLambdaUpdateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        addressBookLambdaUpdateWrapper.set(AddressBook::getIsDefault,0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(addressBookLambdaUpdateWrapper);
        //2、在根据参数中的id，单独为这个地址设置为默认地址
        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success("设置默认地址成功");
    }

    /**
     * 在去结算的页面回显默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefaultAddressBook(){
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        addressBookLambdaQueryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(addressBookLambdaQueryWrapper);
        return R.success(addressBook);
    }

}
