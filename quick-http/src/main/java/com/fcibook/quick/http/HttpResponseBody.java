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
    private byte[] bytes;
    private OnHttpErrorListener mOnHttpErrorListener;

    HttpResponseBody(HttpResponse response,OnHttpErrorListener listener){
        mOnHttpErrorListener = listener;
        stateCode = response.getStatusLine().getStatusCode();
        headers = response.getAllHeaders();
        bytes = bytes(response.getEntity());
        if(QuickHttpController.isDebug){
            QuickHttpController.log("Response content : "+ text());
        }
    }
    private byte[] bytes(HttpEntity entity){
        try {
            return EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            error(e);
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
                String cookie = header.getValue();
                QuickHttpController.log("cookie : "+cookie);
                store.putCookie(cookie);
            }
        }
        if (store.size() > 0) {
            return store;
        }
        return null;
    }

    @Override
    public String text() {
        if(bytes == null){
            return null;
        }
        return new String(bytes, QuickHttpController.DEFAULT_CHARSET);
    }
    @Override
    public byte[] bytes() {
        return bytes;
    }


    private void error(Throwable t){
        if(mOnHttpErrorListener != null){
            mOnHttpErrorListener.onError(t);
        }
    }
}
