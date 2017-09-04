package com.fcibook.quick.http;

import org.apache.http.*;
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
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cc_want on 2017/7/31.
 */
public class QuickHttpController {


    public static int DEFAULT_CONNECTION_TIMEOUT  = 6 * 1000; // timeout in millis
    private final static String DEFAULT_USER_AGENT = "Mozilla/4.0 (compatible; MSIE 7.0; Win32)";
    private final static HttpHost DEFAULT_PROXY = RequestConfig.DEFAULT.getProxy();

    private final Charset defaultCharset = Charset.forName("utf-8");
    private String mUrl;
    private HttpType mHttpType;
    private HttpRequestBase mRequest;
    private List<Header> mHeaders = new ArrayList<Header>();
    private Map<String,Object> mParames= new HashMap<String,Object>();
    private String mBodyContent;
    private ContentType mContentType;
    private String mUserAgent;
    private boolean isKeepAlive = false;
    private HttpHost mProxy;
    private CookieStore cookieStore = new CookieStore();
    private QuickHttpListener mInterface;
    //file
    private File mFile;
    private String mName;
    private String mFileName;
    private Map<String,String> mFileParames= new HashMap<String,String>();

    private boolean isDebug = false;

    public enum  HttpType{
        GET,POST
    }

    public void setDebug(boolean debug){
        isDebug = debug;
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
    public void addParame(String key,Object value){
        mParames.put(key,value);
    }
    public void addParames(Map<String,Object> parames){
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
    public void removeAllCookie(){
        cookieStore.clear();
    }
    public void setListener(QuickHttpListener listener){
        mInterface = listener;
    }

    private void setupParame(){
        StringBuilder buffer = new StringBuilder();
        buffer.append(mUrl);
        int i = 0;
        for (String key:mParames.keySet()) {
            Object value = mParames.get(key);
            if (i == 0)
            {
                buffer.append("?");
            }else{
                buffer.append("&");
            }
            buffer.append(key);
            buffer.append("=");
            buffer.append(URLEncoder.encode(String.valueOf(value)));
            i ++;
        }
        mUrl = buffer.toString();
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
        log("upload file:"+mFile.getName() +"  exists:"+ mFile.exists());
        MultipartEntityBuilder entity = MultipartEntityBuilder.create()
                .seContentType(ContentType.MULTIPART_FORM_DATA)
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .addBinaryBody(mName,mFile,ContentType.DEFAULT_BINARY,mFileName) //uploadFile对应服务端类的同名属性<File类型>
                .setCharset(defaultCharset);

        for (String key:mFileParames.keySet()) {
            String value = mFileParames.get(key);
            entity.addTextBody(key,value);
        }
        httpPost.setEntity(entity.build());
    }
    private void setupUrlEncodedFormEntity(HttpPost httpPost){
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        for (String key:mParames.keySet()) {
            Object value = mParames.get(key);
            nvps.add(new BasicNameValuePair(key, String.valueOf(value)));
        }
        UrlEncodedFormEntity entity;
        try {
            entity = new UrlEncodedFormEntity(nvps);
            httpPost.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void setupBodyContentFormEntity(HttpPost httpPost){
        StringEntity entity = new StringEntity(mBodyContent,mContentType);
        httpPost.setEntity(entity);
    }
    private void bindRequest(){
        setDefaultParameter();
        if(mHttpType == HttpType.GET){
            setupParame();
            log("GET "+mUrl);
            mRequest = new HttpGet(mUrl);
        }else if(mHttpType == HttpType.POST){
            HttpPost httpPost= new HttpPost(mUrl);
            setupEntity(httpPost);
            log("POST "+mUrl);
            mRequest = httpPost;
        }
        mRequest.setConfig(getDefaultRequestConfig());
        setupHeaders();
    }
    private RequestConfig getDefaultRequestConfig(){
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(DEFAULT_CONNECTION_TIMEOUT)
                .setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT)
                .setSocketTimeout(DEFAULT_CONNECTION_TIMEOUT)
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
        if(!containsHeaderName("Cookie")){
            addHeader("Cookie",cookieStore.toString());
        }
        if(mProxy == null){
            mProxy = DEFAULT_PROXY;
        }
    }
    private void setupHeaders(){
        Header[] headers = new Header[mHeaders.size()];
        mHeaders.toArray(headers);
        mRequest.setHeaders(headers);
    }
    private void safeClose(CloseableHttpResponse response){
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


    private void handlerResponseHeaders(HttpResponse response){
        Header[] headers = response.getAllHeaders();
        handlerSetCooikes(headers);
        log("headers count: "+ headers.length);
    }
    private void handlerSetCooikes(Header[] headers){
        CookieStore store = new CookieStore();
        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            log("headers ["+i+"]  "+ header);
            if(header.getName().equals("Set-Cookie")){
                store.putCookie(header.getValue());
            }
        }
        if(mInterface != null && store.size() > 0){
            mInterface.onSetCookie(store);
        }
    }
    public String text(){
        bindRequest();
        CloseableHttpResponse response = null;
        try {
            response = execute();
            log(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, defaultCharset);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            safeClose(response);
        }
        return "";
    }
    public byte[] bytes(){
        bindRequest();
        CloseableHttpResponse response = null;
        try {
            response = execute();
            log(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            handlerResponseHeaders(response);
            return EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            safeClose(response);
        }
        return null;
    }


    private void log(Object message){
        if(!isDebug) {
            return;
        }
        log(String.valueOf(message));
    }
    private void log(String message){
        if(!isDebug) {
            return;
        }
        System.out.println(message);
    }
}
