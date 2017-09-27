package com.fcibook.quick.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by cc_want on 2017/9/12.
 */
public class HttpResponseBody implements ResponseBody {

    private int stateCode;
    private Header[] headers;
    private String text;
    private byte[] bytes;

    HttpResponseBody(HttpResponse response){
        stateCode = response.getStatusLine().getStatusCode();
        headers = response.getAllHeaders();
        bytes = bytes(response.getEntity());
        text = new String(bytes, QuickHttpController.DEFAULT_CHARSET);
    }
    private String text(HttpEntity entity){
        try {
            return EntityUtils.toString(entity, QuickHttpController.DEFAULT_CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private byte[] bytes(HttpEntity entity){
        try {
            return EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getStateCode(){
        return stateCode;
    }

    @Override
    public CookieStore getCookie() {
        CookieStore store = new CookieStore();
        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            if (header.getName().equals("Set-Cookie")) {
                store.putCookie(header.getValue());
            }
        }
        if (store.size() > 0) {
            return store;
        }
        return null;
    }

    @Override
    public String text() {
        return text;
    }
    @Override
    public byte[] bytes() {
        return bytes;
    }
}
