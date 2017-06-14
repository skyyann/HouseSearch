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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        String url = "http://" + cityCode + ".58.com/pinpaigongyu/pn/1/?minprice=" + minPrice
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
    public List<HouseInfo> HouseSearch(String cityCode, String minPrice, String maxPrice, String page, String area, String subway) {
        if (Integer.parseInt(minPrice) > Integer.parseInt(maxPrice)) {
            return null;
        }

        List<HouseInfo> lstHouseInfo = new ArrayList<HouseInfo>();

        //构建线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

        for (int i = Integer.parseInt(page); i < Integer.parseInt(page) + 5; i++) {
            //构建URL
            String url = "http://" + cityCode + ".58.com/pinpaigongyu/pn/" + i + "/?minprice=" + minPrice
                    + "_" + maxPrice + area + subway;

            fixedThreadPool.execute(() -> {
                CloseableHttpClient client = HttpClients.createDefault();
                try {
                    //构建Httpclient，爬取url
                    HttpGet get = new HttpGet(url);

                    //设置请求头
                    get.setHeader("Accept", "text/html, application/xhtml+xml, image/jxr, */*");
                    get.setHeader("Accept-Language", "zh-Hans-CN, zh-Hans; q=0.8, en-US; q=0.5, en; q=0.3");
                    get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

                    //获取响应
                    CloseableHttpResponse response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    String html = EntityUtils.toString(entity, "UTF-8");

                    //解析html
                    Document doc = Jsoup.parse(html);
                    Elements lists = doc.getElementsByAttribute("logr");
                    for (Element list : lists) {
                        //将关键信息提取出来
                        HouseInfo houseInfo = new HouseInfo();
                        String[] houseInfoArray = list.getElementsByTag("h2").first().text().split(" ");
                        houseInfo.setHouseTitle(list.getElementsByTag("h2").first().text());
                        houseInfo.setHouseURL("http://" + cityCode + ".58.com" + list.getElementsByTag("a").first().attributes().get("href"));
                        houseInfo.setMoney(list.getElementsByClass("money").tagName("b").text());
                        houseInfo.setHouseLocation(houseInfoArray[1]);
                        lstHouseInfo.add(houseInfo);
                    }
                } catch (IOException ex) {

                }
            });
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