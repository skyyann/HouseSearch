package com.sky.controller;

import com.sky.model.HouseInfo;

import org.jsoup.Connection;
import org.jsoup.Connection.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Sky on 2017/4/20.
 */
@Controller
public class HouseController {
    private static final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(30);
    private static String newUrl;
    /**
     * 返回首页，加载地图
     */
    @RequestMapping("/")
    public String Index() {
        return "index";
    }

    /**
     * 获取总页数，返回给前台
     * 参数
     *
     * @param cityCode 城市
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/GetTotalPages", method = RequestMethod.POST)
    public int GetTotalPages(String cityCode, int minPrice, int maxPrice, String area, String subway) {
        //构建URL
        String oldUrl = "http://" + cityCode + ".58.com";
        Connection conn = Jsoup.connect(oldUrl);
        int pages = 0;
        try {
            Response response = conn.method(Method.GET).execute();
            newUrl = response.url().toString() + "/pinpaigongyu/pn/";
            String nowUrl = newUrl + "1/?minprice=" + minPrice + "_" + maxPrice + area + subway;
            Document doc = Jsoup.connect(nowUrl).get();
            int listsum = Integer.valueOf(doc.getElementsByClass("listsum").select("em").text());
            pages = listsum % 20 == 0 ? listsum / 20 : listsum / 20 + 1;  //计算页数
        } catch (IOException ex) {

        }
        return pages;
    }

    /**
     * 从58同城获取房租信息，解析html并封装为List，返回给前台
     * 参数
     *
     * @param cityCode 城市
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param page     当前页数
     * @return list 返回封装的信息
     */
    @ResponseBody
    @RequestMapping(value = "/HouseSearch", method = RequestMethod.POST)
    public List<HouseInfo> HouseSearch(String cityCode, int minPrice,
                                       int maxPrice, int page,
                                       String area, String subway) {

        List<HouseInfo> lstHouseInfo = new ArrayList<HouseInfo>();

        for (int i = page; i < page + 5; i++) {
            String url = newUrl + i + "/?minprice=" + minPrice + "_" + maxPrice + area + subway;

            Future<List<HouseInfo>> result = fixedThreadPool.submit(new Crawler(url));
            try {
                lstHouseInfo.addAll(result.get());
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e.getMessage());
            }
        }
        return lstHouseInfo;
    }
}