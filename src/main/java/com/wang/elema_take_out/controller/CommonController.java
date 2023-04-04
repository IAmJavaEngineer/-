package com.wang.elema_take_out.controller;

import com.wang.elema_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author wjh
 * @create 2023-03-18
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {


    @Value("${basePath}")
    private String basePath;

    /**
     * 文件上传
     * @param file 参数类型是MultipartFile，并且这个参数名要和前端传过来的参数名一致
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file只是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件删除
        log.info(file.toString());
        //原始文件名
        String filename = file.getOriginalFilename();   //带后缀的文件名、.jpg、.png....
        //为了防止文件名重复，拼接一个日期格式的字符串
        LocalDateTime now = LocalDateTime.now();
        String fileTime = now.toString();
        //        将日期格式的冒号改为.
        fileTime = fileTime.replace(':','.');
        System.out.println(fileTime);
        filename = fileTime+filename;


        //判断basePath目录是否存在
        File path = new File(basePath);
        if (!path.exists()){
            //如果不存在，就创建目录
            path.mkdirs();      //mkdirs()创建多级目录
        }
        try {
//            将临时文件转存到指定目录下
            file.transferTo(new File(basePath+filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //返回文件名
        return R.success(filename);

    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){


        try {
            //创建输入流，从文件中读取数据
            FileInputStream fileInputStream = new FileInputStream(basePath+name);

            //创建输出流，响应回浏览器，在浏览器页面上显示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //设置响应的数据类型为图片类型
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
