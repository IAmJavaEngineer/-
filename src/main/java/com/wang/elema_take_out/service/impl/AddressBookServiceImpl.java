package com.wang.elema_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.elema_take_out.domain.AddressBook;
import com.wang.elema_take_out.domain.User;
import com.wang.elema_take_out.mapper.AddressBookMapper;
import com.wang.elema_take_out.mapper.UserMapper;
import com.wang.elema_take_out.service.AddressBookService;
import com.wang.elema_take_out.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wjh
 * @create 2023-03-15
 */
@Service
@Transactional
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
