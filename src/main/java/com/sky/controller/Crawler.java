package com.sky.controller;

import com.sky.model.HouseInfo;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Crawler implements Callable<List<HouseInfo>> {
    private String url;
    private List<HouseInfo> lstHouseInfo;
    public Crawler(String url){
        this.url = url;
    }

    public List<HouseInfo> call() {
        lstHouseInfo = new ArrayList<HouseInfo>();
        CloseableHttpClient client = HttpClients.createDefault();
        Lock lock = new ReentrantLock();
        HttpGet get = new HttpGet(url);
        //设置请求头
        get.setHeader("Accept", "text/html, application/xhtml+xml, image/jxr, */*");
        get.setHeader("Accept-Language", "zh-Hans-CN, zh-Hans; q=0.8, en-US; q=0.5, en; q=0.3");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        try{
            //获取响应
            CloseableHttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            String html = EntityUtils.toString(entity, "UTF-8");
            //解析html
            Document doc = Jsoup.parse(html);
            Elements lists = doc.getElementsByAttribute("logr");
            lock.lock();
            try{
                for (Element list : lists) {
                    //将关键信息提取出来
                    HouseInfo houseInfo = new HouseInfo();
                    String[] houseInfoArray = list.getElementsByTag("h2").first().text().split(" ");
                    houseInfo.setHouseTitle(list.getElementsByTag("h2").first().text());
                    houseInfo.setHouseURL("http://" + "bj" + ".58.com" + list.getElementsByTag("a").first().attributes().get("href"));
                    houseInfo.setMoney(list.getElementsByClass("money").tagName("b").text());
                    houseInfo.setHouseLocation(houseInfoArray[1]);
                    lstHouseInfo.add(houseInfo);
                }
            }finally {
                lock.unlock();
            }

        }catch (IOException e){
            System.out.println( e.getMessage());
        }
        return lstHouseInfo;
    }
}
