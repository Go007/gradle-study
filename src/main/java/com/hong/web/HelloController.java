package com.hong.web;

import com.hong.service.JdbcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wanghong
 * @date 2019/08/14 14:15
 **/
@Controller
public class HelloController {

    @Autowired
    private JdbcService jdbcService;

    @GetMapping("/")
    @ResponseBody
    public Object Home() {
       // String sql = "SELECT * FROM t_user WHERE account = 'wanghong'";
       // return jdbcService.execute(sql);
        jdbcService.benchmarkTest();
        return null;
    }

}
