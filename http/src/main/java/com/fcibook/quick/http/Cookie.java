package com.fcibook.quick.http;

/**
 * Created by cc_want on 2017/8/2.
 */
public class Cookie {

    private String name;
    private String value;

    public Cookie(String name,String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(name);
        sb.append("=");
        sb.append(value);
        return sb.toString();
    }
}
