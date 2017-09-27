package com.fcibook.quick.http;

/**
 * Created by cc_want on 2017/9/12.
 */
public interface ResponseBody {

     int getStateCode();

     CookieStore getCookie();

     String text();

     byte[] bytes();
}
