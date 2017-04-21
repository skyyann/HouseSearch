package com.sky.model;

/**
 * Created by Sky on 2017/4/21.
 */
public class HouseInfo {
    private String houseTitle;
    private String houseURL;
    private String money;

    public String getHouseTitle() {
        return houseTitle;
    }

    public void setHouseTitle(String houseTitle) {
        this.houseTitle = houseTitle;
    }

    public String getHouseURL() {
        return houseURL;
    }

    public void setHouseURL(String houseURL) {
        this.houseURL = houseURL;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getHouseLocation() {
        return houseLocation;
    }

    public void setHouseLocation(String houseLocation) {
        this.houseLocation = houseLocation;
    }

    private String houseLocation;
}
