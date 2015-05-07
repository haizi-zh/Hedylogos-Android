package com.lv.net;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.lv.Listener.FetchListener;
import com.lv.Listener.LoginSuccessListener;
import com.lv.Utils.Config;
import com.lv.bean.Message;
import com.lv.im.IMClient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by q on 2015/4/27.
 */
public class HttpUtils {
    private static SyncHttpClient client = new SyncHttpClient();
    static ExecutorService exec = Executors.newFixedThreadPool(5);

    public static void login(final String username, final LoginSuccessListener listen) {

        exec.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject obj = new JSONObject();
                try {
                    String cid = null;
                    while (true) {
                        if (IMClient.getInstance().getCid() != null) {
                            cid = IMClient.getInstance().getCid();
                            break;
                        }
                    }
                    obj.put("userId", Long.parseLong(username));
                    obj.put("regId", cid);
                    if (Config.isDebug){
                        Log.i(Config.TAG,"login:" + obj.toString());
                    }
                    HttpPost post = new HttpPost(Config.LOGIN_URL);
                    HttpResponse httpResponse = null;
                    StringEntity entity = new StringEntity(obj.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    DefaultHttpClient defaultHttpClient= new DefaultHttpClient();
                    defaultHttpClient.getParams().setParameter("Timeout",5*1000);
                    httpResponse = defaultHttpClient.execute(post);
                    final int code = httpResponse.getStatusLine().getStatusCode();
                    if (Config.isDebug){
                        Log.i(Config.TAG,"Status code:" + code);
                    }
                    if (code == 200) {
                        IMClient.getInstance().setCurrentUser(username);
                        IMClient.getInstance().initDB();
                        listen.OnSuccess();
                    } else {
                        listen.OnFalied(code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void FetchNewMsg(String user, final FetchListener listener) {
        final String path = Config.FETCH_URL + user;
        final RequestParams params = new RequestParams();
        params.put("userId", user);
        exec.execute(new Runnable() {
            @Override
            public void run() {

                client.get(path, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                      if (Config.isDebug){
                            Log.i(Config.TAG, "error code:" + i);
                        }
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        try {
                            System.out.println(Thread.currentThread());
                            if (Config.isDebug){
                                Log.i(Config.TAG,"fetch:" + s);
                            }
                            JSONObject object = new JSONObject(s);
                            JSONArray array = object.getJSONArray("result");
                            List<Message> list = new ArrayList<Message>();
                            for (int j = 0; j < array.length(); j++) {
                                Message msg = JSON.parseObject(array.getJSONObject(j).toString(), Message.class);
                                list.add(msg);
                            }
                            if (list.size()>0) {
                                listener.OnMsgArrive(list);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public static void postack(final JSONArray array, String userid) {
        final String url = Config.ACK_URL + userid + "/ack";
        final JSONObject obj = new JSONObject();
        try {
            obj.put("msgList", array);
            IMClient.getInstance().clearackList();
            if (Config.isDebug){
                Log.i(Config.TAG,"ack : " + obj.toString());
            }
            IMClient.getInstance().clearackList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        exec.execute(new Runnable() {
            @Override
            public void run() {
                HttpPost post = new HttpPost(url);
                HttpResponse httpResponse = null;
                try {
                    StringEntity entity = new StringEntity(obj.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    httpResponse = new DefaultHttpClient().execute(post);
                    System.out.println("send status code:" + httpResponse.getStatusLine().getStatusCode());
                    HttpEntity res = httpResponse.getEntity();
                    if (Config.isDebug){
                        Log.i(Config.TAG,"ack Result : " + EntityUtils.toString(res));
                        Log.i(Config.TAG,"list size:" + array.length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

    }

    public static void NetSpeedTest() {
        exec.execute(new Runnable() {
            @Override
            public void run() {
                List<String> urlList = new ArrayList<String>();
                urlList.add("www.baidu.com");
                urlList.add("www.sina.com.cn");
                urlList.add("www.163.com");
                Process process = null;
                String result = "";
                String fastest="";
                long temp=9999;
                for (String url : urlList) {
                    long start = 0;
                    long end = 0;
                    try {
                        start = System.currentTimeMillis();
                        process = Runtime.getRuntime().exec("/system/bin/ping -c 1 "+url);
                        int status = process.waitFor();
                        if (status == 0) {
                            result = "success";


                        } else {
                            result = Integer.toString(status);
                        }
                        end = System.currentTimeMillis();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    long time=end - start;
                    if (Config.isDebug){
                        Log.i(Config.TAG,"result " + result);
                        Log.i(Config.TAG,url+" ping time:" + time);
                    }
                    if (time<temp){
                        temp=time;
                        fastest=url;
                    }
                }
                if (Config.isDebug){
                    Log.i(Config.TAG,"fastest url "+fastest);
                }
            }
        });


    }
    public interface tokenget {
        public void OnSuccess(String key, String token);
    }

    public static void getToken(final tokenget listener) {
        String token = null;
        exec.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject object = new JSONObject();
                try {
                    object.put("action", 1);
                    HttpPost post = new HttpPost("http://hedy.zephyre.me/upload/token-generator");
                    HttpResponse httpResponse = null;
                    StringEntity entity = new StringEntity(object.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    httpResponse = new DefaultHttpClient().execute(post);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(httpResponse.getEntity());
                        JSONObject res = new JSONObject(result);
                        JSONObject obj = res.getJSONObject("result");
                        String key = obj.getString("key");
                        String token = obj.getString("token");
                        listener.OnSuccess(key, token);
                    } else
                    if (Config.isDebug){
                        Log.i(Config.TAG,"Tokenget Error :" + httpResponse.getStatusLine().getStatusCode());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
