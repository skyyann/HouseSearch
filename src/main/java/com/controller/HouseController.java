package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Sky on 2017/4/20.
 */
@Controller
public class HouseController {
    @RequestMapping("/index")
    public String Index(){
        return "index";
    }
    @RequestMapping("/HouseSearch")
    public String HouseSearch(String cityCode, String minPrice, String maxPrice){
        return "index";
    }
}
