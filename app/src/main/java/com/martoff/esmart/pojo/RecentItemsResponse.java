package com.martoff.esmart.pojo;

/**
 * Created by Safeer on 26-May-16.
 */
public class RecentItemsResponse {

    double id, bid_exp, disc_amt, disc_pct;
    int status, is_saved, sid, bid_current, disc_valid, rate_curid, type, format, bid_next, bids;
    String unit_label, pics, bid_end, name, rate;

    public double getId() {
        return id;
    }

    public void setId(double id) {
        this.id = id;
    }

    public double getBid_exp() {
        return bid_exp;
    }

    public void setBid_exp(double bid_exp) {
        this.bid_exp = bid_exp;
    }

    public double getDisc_pct() {
        return disc_pct;
    }

    public void setDisc_pct(double disc_pct) {
        this.disc_pct = disc_pct;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public double getDisc_amt() {
        return disc_amt;
    }

    public void setDisc_amt(double disc_amt) {
        this.disc_amt = disc_amt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIs_saved() {
        return is_saved;
    }

    public void setIs_saved(int is_saved) {
        this.is_saved = is_saved;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getBid_current() {
        return bid_current;
    }

    public void setBid_current(int bid_current) {
        this.bid_current = bid_current;
    }

    public int getDisc_valid() {
        return disc_valid;
    }

    public void setDisc_valid(int disc_valid) {
        this.disc_valid = disc_valid;
    }

    public int getRate_curid() {
        return rate_curid;
    }

    public void setRate_curid(int rate_curid) {
        this.rate_curid = rate_curid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getBid_next() {
        return bid_next;
    }

    public void setBid_next(int bid_next) {
        this.bid_next = bid_next;
    }

    public int getBids() {
        return bids;
    }

    public void setBids(int bids) {
        this.bids = bids;
    }

    public String getUnit_label() {
        return unit_label;
    }

    public void setUnit_label(String unit_label) {
        this.unit_label = unit_label;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public String getBid_end() {
        return bid_end;
    }

    public void setBid_end(String bid_end) {
        this.bid_end = bid_end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
