package com.fcibook.quick.http;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cc_want on 2017/7/31.
 */
public class QuickHttpController {

    public final static Charset DEFAULT_CHARSET = Charset.forName("utf-8");
    public final static int DEFAULT_CONNECTION_TIMEOUT  = 6 * 1000; // timeout in millis
    private final static String DEFAULT_USER_AGENT = "Mozilla/4.0 (compatible; MSIE 7.0; Win32)";
    private final static HttpHost DEFAULT_PROXY = RequestConfig.DEFAULT.getProxy();

    private String mUrl;
    private int mConnectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private HttpType mHttpType;
    private HttpRequestBase mRequest;
    private List<Header> mHeaders = new ArrayList<Header>();
    private Map<String,String> mParames= new HashMap<String,String>();
    private String mBodyContent;
    private ContentType mContentType;
    private String mUserAgent;
    private boolean isKeepAlive = false;
    private HttpHost mProxy;
    private CookieStore cookieStore = new CookieStore();
    //file
    private File mFile;
    private String mName;
    private String mFileName;
    private Map<String,String> mFileParames= new HashMap<String,String>();
    //listener
    private OnHttpErrorListener mOnHttpErrorListener;

    protected static boolean isDebug = false;

    public enum  HttpType{
        GET,POST
    }

    public void setDebug(boolean debug){
        isDebug = debug;
    }
    public void setOnHttpErrorListener(OnHttpErrorListener listener){
        mOnHttpErrorListener = listener;
    }
    public void setUrl(String url){
        mUrl = url;
    }
    public void get(){
        mHttpType = HttpType.GET;
    }
    public void post(){
        mHttpType = HttpType.POST;
    }
    public void setFile(File file,String name,String fileName){
        mFile = file;
        mName = name;
        mFileName = fileName;
    }
    public void addFileParame(String key,String value){
        mFileParames.put(key,value);
    }
    public void addHeader(String name,String value){
        mHeaders.add(new BasicHeader(name,value));
    }
    public void addParame(String key,String value){
        mParames.put(key,value);
    }
    public void addParames(Map<String,String> parames){
        mParames.putAll(parames);
    }
    public void setBodyContent(String content){
        mBodyContent = content;
    }
    public void setContentType(ContentType contentType){
        mContentType = contentType;
    }
    public void setUserAgent(String userAgent){
        mUserAgent = userAgent;
    }
    public void keepAlive(){
        isKeepAlive = true;
    }
    public void setProxy(String hostname, int port){
        mProxy = new HttpHost(hostname,port);
    }
    public void addCookie(String name,String value){
        cookieStore.addCookie(name,value);
    }
    public void addCookies(Map<String,String> cookies){
        cookieStore.addCookies(cookies);
    }
    public void addCookie(Cookie cookie){
        cookieStore.addCookie(cookie);
    }
    public void addCookies(List<Cookie> cookies){
        cookieStore.addCookies(cookies);
    }
    public void removeAllCookie(){
        cookieStore.clear();
    }
    public void setConnectionTimeout(int timeout){
        mConnectionTimeout = timeout;
    }

    private void setupEntity( HttpPost httpPost){
        if (mParames != null && mParames.size() >0) {
            setupUrlEncodedFormEntity(httpPost);
        }else if (mBodyContent != null) {
            setupBodyContentFormEntity(httpPost);
        }else if(mFile != null){
            setupMultipartEntity(httpPost);
        }
    }
    private void setupMultipartEntity(HttpPost httpPost){
        if(isDebug){
            log("Request upload file:"+mFile.getName() +"  exists:"+ mFile.exists());
        }
        MultipartEntityBuilder entity = MultipartEntityBuilder.create()
                .seContentType(ContentType.MULTIPART_FORM_DATA)
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addBinaryBody(mName,mFile,ContentType.DEFAULT_BINARY,mFileName) //uploadFile对应服务端类的同名属性<File类型>
                .setCharset(DEFAULT_CHARSET);

        for (String key:mFileParames.keySet()) {
            String value = mFileParames.get(key);
            entity.addTextBody(key,value);
        }
        httpPost.setEntity(entity.build());
    }
    private void setupUrlEncodedFormEntity(HttpPost httpPost){
        List<NameValuePair> nvps = new ArrayList <NameValuePair>();
        for (String key:mParames.keySet()) {
            String value = mParames.get(key);
            if(isDebug){
                log("Request parame <key>: " + key + " <value>:"+value);
            }
            nvps.add(new BasicNameValuePair(key, value));
        }
        UrlEncodedFormEntity entity;
        try {
            entity = new UrlEncodedFormEntity(nvps);
            httpPost.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            error(e);
        }
    }
    private void setupBodyContentFormEntity(HttpPost httpPost){
        if(isDebug){
            log("Request content: "+mBodyContent);
        }
        StringEntity entity = new StringEntity(mBodyContent,mContentType);
        httpPost.setEntity(entity);
    }
    private void bindRequest(){
        setDefaultParameter();
        if(mHttpType == HttpType.GET){
            QuickURL quickURL = new QuickURL(mUrl,mParames);
            mUrl = quickURL.fullUrl();
            if(isDebug){
                log("GET "+mUrl);
            }
            mRequest = new HttpGet(mUrl);
        }else if(mHttpType == HttpType.POST){
            HttpPost httpPost= new HttpPost(mUrl);
            if(isDebug){
                log("POST "+mUrl);
            }
            setupEntity(httpPost);
            mRequest = httpPost;
        }
        mRequest.setConfig(getDefaultRequestConfig());
        setupHeaders();
    }
    private RequestConfig getDefaultRequestConfig(){
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(mConnectionTimeout)
                .setConnectTimeout(mConnectionTimeout)
                .setSocketTimeout(mConnectionTimeout)
                .setProxy(mProxy)
                .build();
        return requestConfig;
    }
    private boolean containsHeaderName(String name){
        for (int i = 0; i < mHeaders.size(); i++) {
            Header header = mHeaders.get(i);
            if(header.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
    /**
     * set default parameter
     */
    private void setDefaultParameter(){
        if(mUserAgent == null){
            addHeader("User-Agent",DEFAULT_USER_AGENT);
        }
        if(isKeepAlive){
            addHeader("Proxy-Connection", "Keep-Alive");
        }
        if(cookieStore.size() > 0 && !containsHeaderName("Cookie")){
            addHeader("Cookie",cookieStore.toString());
        }
        if(mProxy == null){
            mProxy = DEFAULT_PROXY;
        }
    }
    private void setupHeaders(){
        if(isDebug){
            log("Request headers size: "+mHeaders.size());
            for (Header header:mHeaders) {
                log("Request header <name>: " + header.getName() +" <value>:"+header.getValue());
            }
        }
        Header[] headers = new Header[mHeaders.size()];
        mHeaders.toArray(headers);
        mRequest.setHeaders(headers);
    }
    private void safeClose(CloseableHttpResponse response){
        if(response == null){
            return;
        }
        try {
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private CloseableHttpClient createHttpClient(){
        HttpClientBuilder builder =  HttpClientBuilder.create();
        return builder.build();
    }

    private CloseableHttpResponse execute() throws IOException {
        CloseableHttpClient httpclient = createHttpClient();
        return httpclient.execute(mRequest);
    }
    public ResponseBody body(){
        bindRequest();
        CloseableHttpResponse response = null;
        try {
            response = execute();
            return new HttpResponseBody(response,mOnHttpErrorListener);
        } catch (IOException e) {
            error(e);
        } finally {
            safeClose(response);
        }
        return null;
    }
    public String text(){
        ResponseBody body = body();
        if(body == null){
            return null;
        }
        return body.text();
    }
    public byte[] bytes(){
        ResponseBody body = body();
        if(body == null){
            return null;
        }
        return body.bytes();
    }
    private void error(Throwable t){
        if(mOnHttpErrorListener != null){
            mOnHttpErrorListener.onError(t);
        }
    }
    protected static void log(Object message){
        log(String.valueOf(message));
    }
    protected static void log(String message){
        if(!isDebug) {
            return;
        }
        message = message.replaceAll("\\r|\\n","");
        if(message.length() > 200){
            message = message.substring(0,200);
        }
        System.out.println("[QuickHttp] "+message);
    }
}
