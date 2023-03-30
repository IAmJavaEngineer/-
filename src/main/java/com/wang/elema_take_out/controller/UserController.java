package com.wang.elema_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wang.elema_take_out.common.R;
import com.wang.elema_take_out.domain.User;
import com.wang.elema_take_out.service.UserService;
import com.wang.elema_take_out.utils.SMSUtils;
import com.wang.elema_take_out.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author wjh
 * @create 2023-03-28
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //1、保存用户手机号
        String phone = user.getPhone();
        //判断手机号是否为空
        if(Strings.isNotEmpty(phone)){
            //2、生成4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info(code);
            //3、调用阿里云提供的短信服务api完成发送短信
            //4个参数分别是签名、模板CODE、手机号码和验证码
//            SMSUtils.sendMessage("王同学博客","SMS_274970468",phone,code);
            //4、使用session保存用户手机号和验证码
            session.setAttribute(phone,code);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        //获取用户输入的手机号
        String phone = (String) map.get("phone");
        //获取用户输入的验证码
        String code = (String) map.get("code");
        //获取session中的验证码
        String sessionCode = (String) session.getAttribute(phone);
        //判断两个验证码是否相等
        if (sessionCode!=null && sessionCode.equals(code)){
            //如果相等的话就查询这个用户是否是新用户，如果是就自动为他注册
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(userLambdaQueryWrapper);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            //把用户id存放到session中
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("验证码发送失败");
    }
}
