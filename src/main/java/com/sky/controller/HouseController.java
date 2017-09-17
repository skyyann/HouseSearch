package com.sky.controller;

import com.sky.model.HouseInfo;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Sky on 2017/4/20.
 */
@Controller
public class HouseController {
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
    public int GetTotalPages(String cityCode, String minPrice, String maxPrice, String area, String subway) {
        //构建URL
        String url = "http://" + "bj" + ".58.com/pinpaigongyu/pn/1/?minprice=" + minPrice
                + "_" + maxPrice + area + subway;
        int pages = 0;
        try {
            Document doc = Jsoup.connect(url).get();
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
    public List<HouseInfo> HouseSearch(String cityCode, String minPrice,
                                       String maxPrice, String page,
                                       String area, String subway){
        if (Integer.parseInt(minPrice) > Integer.parseInt(maxPrice)) {
            return null;
        }

        List<HouseInfo> lstHouseInfo = new ArrayList<HouseInfo>();

        //构建线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

        for (int i = Integer.parseInt(page); i < Integer.parseInt(page) + 5; i++) {
            //构建URL
            String url = "http://" + "bj" + ".58.com/pinpaigongyu/pn/" + i + "/?minprice=" + minPrice
                    + "_" + maxPrice + area + subway;

            Future<List<HouseInfo>> result = fixedThreadPool.submit(new Crawler(url));
            try{
                lstHouseInfo.addAll(result.get());
            }catch (ExecutionException e){
                System.out.println(e.getMessage());
            }catch (InterruptedException e){
                System.out.println(e.getMessage());
            }
        }
        fixedThreadPool.shutdown();
        try {
            while (!fixedThreadPool.isTerminated()) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lstHouseInfo;
    }
}