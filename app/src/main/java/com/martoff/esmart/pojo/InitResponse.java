package com.martoff.esmart.pojo;

import java.util.ArrayList;

/**
 * Created by imran on 16-May-16.
 */
public class InitResponse {

    int uid;
    String notifs;
    ArrayList<ResponseMods> response_mods;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getNotifs() {
        return notifs;
    }

    public void setNotifs(String notifs) {
        this.notifs = notifs;
    }

    public ArrayList<ResponseMods> getResponse_mods() {
        return response_mods;
    }

    public void setResponse_mods(ArrayList<ResponseMods> response_mods) {
        this.response_mods = response_mods;
    }
}
