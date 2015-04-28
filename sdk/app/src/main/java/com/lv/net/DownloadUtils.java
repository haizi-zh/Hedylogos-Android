package com.lv.net;

import com.lv.Utils.TimeUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by q on 2015/4/14.
 */
public class DownloadUtils {
    private static AsyncHttpClient client = new AsyncHttpClient();
    static {
        client.setTimeout(11000);   //设置链接超时，如果不设置，默认为10s
    }

    public static void get(String urlString, AsyncHttpResponseHandler res)    //用一个完整url获取一个string对象
    {
        client.get(getRealUrl(urlString), res);
    }

    public static void get(String urlString, RequestParams params, AsyncHttpResponseHandler res)   //url里面带参数
    {
        client.get(getRealUrl(urlString), params, res);
    }

    public static void get(String urlString, JsonHttpResponseHandler res)   //不带参数，获取json对象或者数组
    {
        client.get(getRealUrl(urlString), res);
    }

    public static void get(String urlString, RequestParams params, JsonHttpResponseHandler res)   //带参数，获取json对象或者数组
    {
        client.get(getRealUrl(urlString), params, res);
    }

    public static void get(String uString, BinaryHttpResponseHandler bHandler)   //下载数据使用，会返回byte数据
    {
        client.get(getRealUrl(uString), bHandler);
    }

    public static AsyncHttpClient getClient() {
        return client;
    }

    private static String getRealUrl(String url) {
        String token = getDownLoadToken();
        String realUrl = url + TimeUtils.getTimestamp() + token;
        return realUrl;
    }

    private static String getDownLoadToken() {

        return null;
    }
}
