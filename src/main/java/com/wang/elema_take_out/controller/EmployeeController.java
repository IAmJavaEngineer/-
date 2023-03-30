package com.wang.elema_take_out.controller;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.elema_take_out.common.BaseContext;
import com.wang.elema_take_out.common.R;
import com.wang.elema_take_out.domain.Employee;
import com.wang.elema_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author wjh
 * @create 2023-03-15
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest servletRequest, @RequestBody Employee employee){

        //将浏览器传入的密码进行MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //根据浏览器传入的用户名，从数据库中获取相应用户的信息
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //如果用户名为空，sql就不连接eq
        queryWrapper.eq(Strings.isNotEmpty(employee.getUsername()),Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);


        //1、先来验证用户名
//        if (employee.getUsername() != emp.getUsername())  //这种做法是错误的，如果emp没有查出来为null的话，那么emp.getUsername()
        //就会空指针异常
        if (emp == null)
            return R.error("用户名不存在！");

//        2、如果用户名存在，就来验证密码
        if (!password.equals(emp.getPassword()))
            return R.error("密码错误！");

//        3、如果用户名和密码都填写正确，再判断用户是否处于禁用状态，0表示禁用，1表示不禁用
        if(emp.getStatus() == 0)
            return R.error("用户处于禁用状态");

//        如果都验证成功了，那么就将用户id保存在session域中
        HttpSession session = servletRequest.getSession();
        session.setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest servletRequest){
        //1、清理session用户的id
        servletRequest.getSession().removeAttribute("employee");
        //2、返回提示信息，跳转页面在前端已经做了，这里就不跳转页面了，而且这个类加上了@RestController，也是不能跳转页面的
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest servletRequest,@RequestBody Employee employee){

//        为员工初始化密码，密码为身份证后六位,并且进行MD5加密
        String idNumber = employee.getIdNumber();
        String password = idNumber.substring(idNumber.length()-6,idNumber.length());
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        employee.setPassword(password);
        long id = Thread.currentThread().getId();
        Long empId = (Long) servletRequest.getSession().getAttribute("employee");
        log.info("线程id为：{}",id);
//        BaseContext.setCurrentId(empId);
//        为新增员工初始化一些信息

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
////      设置当前新增员工是被谁新增的和修改的
//        Long id = (Long) servletRequest.getSession().getAttribute("employee");
//        employee.setCreateUser(id);
//        employee.setUpdateUser(id);

//        最后才能保存到数据库
        employeeService.save(employee);

        return R.success("添加成功！");
    }


    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){

        System.out.println("page: "+page+",pageSize: "+pageSize+",name: "+name);
//        创建分页构造器
        Page<Employee> employeePage = new Page<>(page,pageSize);
//        创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(name),Employee::getName,name);
//        根据创建时间升序
        queryWrapper.orderByAsc(Employee::getCreateTime);
//        条件查询
        employeeService.page(employeePage,queryWrapper);
        return R.success(employeePage);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
//        获取当前操作的员工id
        Long empId = (Long) request.getSession().getAttribute("employee");
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);
//        BaseContext.setCurrentId(empId);
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());
//        修改员工status字段在前端已经修改过了，这里就不需要修改了
        employeeService.updateById(employee);
//        System.out.println(employee.getUpdateTime());
        return R.success("修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){

        log.info("查询员工信息并回显");
        Employee employee = employeeService.getById(id);

        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到对应信息！");
    }

}
