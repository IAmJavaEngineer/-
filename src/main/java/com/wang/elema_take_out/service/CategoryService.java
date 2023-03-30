package com.wang.elema_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.elema_take_out.domain.Category;
import com.wang.elema_take_out.domain.Employee;

/**
 * @author wjh
 * @create 2023-03-15
 */
public interface CategoryService extends IService<Category> {
    public void remover(Long ids);
}
