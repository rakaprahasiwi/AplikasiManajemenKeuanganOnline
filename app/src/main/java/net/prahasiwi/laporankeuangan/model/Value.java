package net.prahasiwi.laporankeuangan.model;

import java.util.List;


public class Value {

    String value;
    String message;
    String jajal;
    List<MainData> result;

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public List<MainData> getResult() {
        return result;
    }

    public String getJajal() {
        return jajal;
    }
}
