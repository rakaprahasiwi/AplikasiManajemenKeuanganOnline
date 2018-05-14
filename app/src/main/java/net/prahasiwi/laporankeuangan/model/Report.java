package net.prahasiwi.laporankeuangan.model;

/**
 * Created by PRAHASIWI on 20/04/2018.
 */

public class Report {
    String category,value;
    public Report(String category, String value) {
        this.category = category;
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public String getValue() {
        return value;
    }
}
