package com.martoff.esmart.pojo;

/**
 * Created by Safeer on 03-Jun-16.
 */
public class SearchIntellisense {

    int id;
    String menu, name, unit_label;
    Double rate;
    String price_from, filename, mark;
    int type, rate_curid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit_label() {
        return unit_label;
    }

    public void setUnit_label(String unit_label) {
        this.unit_label = unit_label;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getPrice_from() {
        return price_from;
    }

    public void setPrice_from(String price_from) {
        this.price_from = price_from;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRate_curid() {
        return rate_curid;
    }

    public void setRate_curid(int rate_curid) {
        this.rate_curid = rate_curid;
    }
}
