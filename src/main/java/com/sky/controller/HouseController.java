package com.sky.controller;

import com.sky.model.HouseInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sky on 2017/4/20.
 */
@Controller
public class HouseController {
    /**
     * 返回首页，加载地图
     */
    @RequestMapping("/")
    public String Index(){
        return "index";
    }

    /**
     * 获取总页数，返回给前台
     * @param cityCode
     * @param minPrice
     * @param maxPrice
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/GetTotalPages", method = RequestMethod.POST)
    public int GetTotalPages(String cityCode, String minPrice, String maxPrice){
        String url ="http://" + cityCode + ".58.com/pinpaigongyu/pn/1/?minprice=" + minPrice + "_" + maxPrice;
        int pages = 0;
        try{
            Document doc = Jsoup.connect(url).get();
            int listsum = Integer.valueOf(doc.getElementsByClass("listsum").select("em").text());
            pages = listsum % 20 == 0 ? listsum / 20 : listsum / 20 + 1;
        }
        catch(IOException ex){

        }
        return pages;
    }

    /**
     * 从58同城获取房租信息，解析html并封装为List，返回给前台
     * @param cityCode
     * @param minPrice
     * @param maxPrice
     * @param page
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/HouseSearch", method = RequestMethod.POST)
    public List<HouseInfo> HouseSearch(String cityCode, String minPrice, String maxPrice, String page){
        String url ="http://" + cityCode + ".58.com/pinpaigongyu/pn/" + page + "/?minprice=" + minPrice + "_" + maxPrice;
        List<HouseInfo> lstHouseInfo = new ArrayList<HouseInfo>();
        try{
            Document doc = Jsoup.connect(url).get();
            Elements lists = doc.getElementsByAttribute("logr");
            for(Element list : lists){
                HouseInfo houseInfo = new HouseInfo();
                String[] houseInfoArray= list.getElementsByTag("h2").first().text().split(" ");
                houseInfo.setHouseTitle(list.getElementsByTag("h2").first().text());
                houseInfo.setHouseURL("http://" + cityCode + ".58.com" + list.getElementsByTag("a").first().attributes().get("href"));
                houseInfo.setMoney(list.getElementsByClass("money").tagName("b").text());
                houseInfo.setHouseLocation(houseInfoArray[1]);
                lstHouseInfo.add(houseInfo);
            }
        }
        catch(IOException ex){

        }
        return lstHouseInfo;
    }
}