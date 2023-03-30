package com.wang.elema_take_out.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wang.elema_take_out.domain.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wjh
 * @create 2023-03-15
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
