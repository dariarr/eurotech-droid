package com.martoff.esmart.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Safeer on 29-May-16.
 */
public class ItemRecentSearches {

    //    POJO FOR THIS CLASS
    //    [
    //{
    //    "id": 10,
    //        "name": "USB-Flash-Disks-Kingston",
    //        "descrip": null,
    //        "emails": 4
    //},
    //    {
    //        "id": 12,
    //            "name": "massagers-bi-weekly",
    //            "descrip": null,
    //            "emails": 3
    //    }
    //    ]
    int id;
    String name;
    String descrip;
    int email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public int getEmail() {
        return email;
    }

    public void setEmail(int email) {
        this.email = email;
    }
}
