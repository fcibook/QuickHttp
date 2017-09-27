package com.fcibook.quick.http;

import java.net.URLEncoder;
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
    private Map<String,String> mParames= new HashMap<String,String>();

    public QuickURL(String url){
        parse(url.trim());
    }
    public QuickURL(String url,Map<String,String> parames){
        parse(url.trim());
        putParames(parames);
    }
    public void putParame(String key,String value){
        mParames.put(key,value);
    }
    public void putParames(Map<String,String> parames){
        mParames.putAll(parames);
    }
    public String getUrl() {
        return mUrl;
    }
    public Map<String, String> getParames() {
        return mParames;
    }
    public void removeParame(String key){
        mParames.remove(key);
    }
    public void clearParame(){
        mParames.clear();
    }

    public String fullUrl(){
        StringBuilder buffer = new StringBuilder();
        buffer.append(mUrl);
        int i = 0;
        for (String key:mParames.keySet()) {
            String value = mParames.get(key);
            if (i == 0) {
                buffer.append("?");
            }else{
                buffer.append("&");
            }
            buffer.append(key);
            buffer.append("=");
            buffer.append(URLEncoder.encode(value));
            i ++;
        }

        return buffer.toString();
    }
    @Override
    public String toString() {
       return fullUrl();
    }

    private void parse(String url){
        if(hasExistParame(url)){
            parseUrl(url);
        }else{
            mUrl = url;
        }
    }
    private boolean hasExistParame(String url){
        return url.indexOf("?") > 0;
    }
    private void parseUrl(String url){
        int i = url.indexOf("?");
        //1
        String urlText = url.substring(0,i);
        //2
        String parameText = url.substring(i+1,url.length());

        mUrl = urlText;
        parseParame(parameText);
    }
    private void parseParame(String parameText){
        String text = parameText;
        int index;
        while((index = text.indexOf("&")) > 0){
            String p = text.substring(0,index);
            text = text.substring(index + 1,text.length());
            match(p);
        }
        match(text);
    }
    private void match(String text){
        final int m = text.indexOf("=");
        final String key = text.substring(0,m);
        final String value = text.substring(m + 1,text.length());
        putParame(key,value);
    }

}
