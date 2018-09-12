package com.fcibook.quick.http;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by cc_want on 2017/8/2.
 */
public class CookieStore {

    private List<Cookie> mCookies = new ArrayList<Cookie>();

    public void addCookie(Cookie cookie){
        mCookies.add(cookie);
    }
    public void addCookies(Map<String,String> cookies){
        for (Map.Entry<String, String> cookie:cookies.entrySet()) {
            addCookie(cookie.getKey(),cookie.getValue());
        }
    }
    public void addCookies(List<Cookie> cookies){
        mCookies.addAll(cookies);
    }
    public void addCookie(String name,String value){
        mCookies.add(new Cookie(name,value));
    }
    public void putCookie(String cookie){
        String[] e = cookie.split("; ");
        if(e.length <= 0){
            return;
        }
        String he = e[0];
        String[] maps = he.split("=");

        if(maps.length != 2){
            return;
        }
        final String name = maps[0];
        final String value = maps[1];
        if(name != null && name.length() > 0 && !contains(name)){
            mCookies.add(new Cookie(name,value));
        }
    }
    public Cookie getCookie(String name){
        for (int i = 0; i < mCookies.size(); i++) {
            Cookie cookie = mCookies.get(i);
            if(cookie.getName().equals(name)){
                return cookie;
            }
        }
        return null;
    }
    public boolean contains(String name){
        for (int i = 0; i < mCookies.size(); i++) {
            Cookie cookie = mCookies.get(i);
            if(cookie.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
    public void clear(){
        mCookies.clear();
    }
    public int size(){
        return mCookies.size();
    }
    public List<Cookie> getCookies(){
        return mCookies;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mCookies.size(); i++) {
            final Cookie cookie = mCookies.get(i);
            sb.append(cookie.toString());
            if(i < mCookies.size() - 1){
                sb.append("; ");
            }
        }
        return sb.toString();
    }
}
