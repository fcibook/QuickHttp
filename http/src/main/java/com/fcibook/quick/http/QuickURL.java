package com.fcibook.quick.http;

import java.util.HashMap;
import java.util.Map;

/**
 * parse url
 * 1.format clear url
 * 2.format url parameter
 * Created by cc_want on 2017/8/14.
 */
public class QuickURL {

    private String mUrl;
    private Map<String,Object> mParames= new HashMap<String,Object>();

    public QuickURL(String url){
        parse(url.trim());
    }
    public void putParame(String key,String value){
        mParames.put(key,value);
    }
    public String getUrl() {
        return mUrl;
    }
    public Map<String, Object> getParames() {
        return mParames;
    }
    public void removeParame(String key){
        mParames.remove(key);
    }
    public void clearParame(){
        mParames.clear();
    }

    private void parse(String url){
        if(hasExistParame(url)){
            parseParame(url);
        }else{
            mUrl = url;
        }
    }
    private boolean hasExistParame(String url){
        return url.indexOf("?") > 0;
    }
    private void parseParame(String url){
        int i = url.indexOf("?");
        String urlText = url.substring(0,i);
        String parameText = url.substring(i+1,url.length());

        mUrl = urlText;
        getParame(parameText);
    }
    private void getParame(String parameText){
        String text = parameText;
        int index;
        while((index = text.indexOf("&")) > 0){
            String p = text.substring(0,index);
            text = text.substring(index + 1,text.length());
            full(p);
        }
        full(text);
    }
    private void full(String text){
        final int m = text.indexOf("=");
        final String key = text.substring(0,m);
        final String value = text.substring(m + 1,text.length());
        putParame(key,value);
    }

}
