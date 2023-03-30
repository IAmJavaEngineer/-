package com.wang.elema_take_out.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wang.elema_take_out.domain.Dish;
import com.wang.elema_take_out.domain.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wjh
 * @create 2023-03-15
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
