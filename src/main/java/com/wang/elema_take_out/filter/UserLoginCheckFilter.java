package com.wang.elema_take_out.filter;

import com.alibaba.fastjson.JSON;
import com.wang.elema_take_out.common.BaseContext;
import com.wang.elema_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 * @author wjh
 * @create 2023-03-15
 */
@Slf4j
@WebFilter(filterName = "userLoginCheckFilter",urlPatterns = "/*")
@Order(0)
public class UserLoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public synchronized void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        1、获取拦截的请求
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);
//        2、定义不需要拦截的路径
        String[] urls = new String[] {
                "/admin/**",
//                "/admin/employee/**",
//                "/admin/category/**",
//                "/admin/dish/**",
//                "/admin/setmeal/**",
//                "/admin/order/**",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };

//        3、将拦截的路径和urls中的路径进行匹配，如果匹配得上，就是不需要拦截的路径，否则就是需要拦截的路径
        boolean check = check(urls,requestURI);

//        如果匹配得上就放行
        if (check){
            log.info("该路径不需要拦截："+requestURI);
            filterChain.doFilter(request,response);

            //            判断登录状态，如果已登录就直接放行
        }else if(request.getSession().getAttribute("user")!=null){
            log.info("用户已登录，直接放行,id为："+request.getSession().getAttribute("user"));
            long id = Thread.currentThread().getId();
            log.info("线程id为：{}",id);
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);

            //如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        } else {
            log.info("UserLoginCheckFilter已拦截该请求："+requestURI);
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        }
    }

    //验证方法
    public boolean check(String[] urls, String requestURL){
        for (String url : urls) {
            if (PATH_MATCHER.match(url,requestURL)){
                return true;
            }
        }
        return false;
    }
}
