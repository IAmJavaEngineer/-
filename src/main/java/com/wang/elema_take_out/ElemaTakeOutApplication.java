package com.wang.elema_take_out;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement    //开启事务注解的支持
@EnableCaching      //开启缓存注解功能
public class ElemaTakeOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElemaTakeOutApplication.class, args);
    }

}
