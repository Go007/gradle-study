package com.hong.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wanghong
 * @date 2019/08/14 14:15
 **/
@Controller
public class HelloController {

    @GetMapping("/")
    @ResponseBody
    public  String Home() {
        return "Hello";
    }

}
