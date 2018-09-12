package com.fcibook.quick.http;

import org.apache.http.entity.ContentType;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by cc_want on 2017/7/31.
 */
public class QuickHttp {

    private QuickHttpController mController;

    public QuickHttp(){
        mController = new QuickHttpController();
    }
    public QuickHttp debug(){
        mController.setDebug(true);
        return this;
    }
    public QuickHttp url(String url){
        mController.setUrl(url);
        return this;
    }
    public QuickHttp url(QuickURL url){
        mController.setUrl(url.getUrl());
        mController.addParames(url.getParames());
        return this;
    }
    public QuickHttp get(){
        mController.get();
        return this;
    }
    public QuickHttp post(){
        mController.post();
        return this;
    }
    public QuickHttp setContentType(ContentType contentType){
        mController.setContentType(contentType);
        return this;
    }
    public QuickHttp setBodyContent(String content){
        mController.setBodyContent(content);
        return this;
    }

    public QuickHttp setFile(File file){
        mController.setFile(file,"file","");
        return this;
    }
    public QuickHttp addFileParame(String key, String value){
        mController.addFileParame(key,value);
        return this;
    }
    public QuickHttp addParame(String key, String value){
        mController.addParame(key,value);
        return this;
    }
    public QuickHttp addParames(Map<String,String> parames){
        mController.addParames(parames);
        return this;
    }
    public QuickHttp addHeader(String name, String value){
        mController.addHeader(name,value);
        return this;
    }
    public QuickHttp addCookie(String name, String value){
        mController.addCookie(name,value);
        return this;
    }
    public QuickHttp addCookie(Cookie cookie){
        mController.addCookie(cookie);
        return this;
    }
    public QuickHttp addCookies(Map<String,String> cookies){
        mController.addCookies(cookies);
        return this;
    }
    public QuickHttp addCookies(List<Cookie> cookies){
        mController.addCookies(cookies);
        return this;
    }
    public QuickHttp removeAllCookie(){
        mController.removeAllCookie();
        return this;
    }
    public QuickHttp keepAlive(){
        mController.keepAlive();
        return this;
    }
    public QuickHttp setUserAgent(String userAgent){
        mController.setUserAgent(userAgent);
        return this;
    }
    public QuickHttp setProxy(String hostname, int port){
        mController.setProxy(hostname,port);
        return this;
    }
    public QuickHttp setConnectionTimeout(int timeout){
        mController.setConnectionTimeout(timeout);
        return this;
    }
    public QuickHttp setOnHttpErrorListener(OnHttpErrorListener listener){
        mController.setOnHttpErrorListener(listener);
        return this;
    }
    public ResponseBody body(){
        return mController.body();
    }
    public String text(){
        return mController.text();
    }
    public byte[] bytes(){
        return mController.bytes();
    }
}
