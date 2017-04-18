package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Sky on 2017/4/18.
 */
@Controller
public class HelloController {
    @RequestMapping("/index")
    public  String test(){
        return "index";
    }
}
