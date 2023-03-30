package com.wang.elema_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.elema_take_out.domain.Employee;
import com.wang.elema_take_out.mapper.EmployeeMapper;
import com.wang.elema_take_out.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author wjh
 * @create 2023-03-15
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
