package net.prahasiwi.laporankeuangan.model;

import java.util.Date;

/**
 * Created by PRAHASIWI on 29/03/2018.
 */

public class MainData {
        private String id,type,image,value,category,dates,describes;

    public MainData(String id, String type, String image, String value, String category, String date, String describe) {
        this.id = id;
        this.type = type;
        this.image = image;
        this.value = value;
        this.category = category;
        this.dates = date;
        this.describes = describe;
    }


    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getImage() {
        return image;
    }

    public String getValue() {
        return value;
    }

    public String getCategory() {
        return category;
    }

    public String getDates() {
        return dates;
    }

    public String getDescribes() {
        return describes;
    }
}
