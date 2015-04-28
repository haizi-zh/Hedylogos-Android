package com.lv.net;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.lv.Utils.Config;
import com.lv.bean.Message;
import com.lv.bean.MessageBean;
import com.lv.im.IMClient;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by q on 2015/4/27.
 */
public class HttpUtils {
    private static AsyncHttpClient client = new AsyncHttpClient();
    public static void login( final String username, final LoginSuccessListen listen) {
        new  Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject obj =new JSONObject();
                try {
                    System.out.println("run");
                    String cid=null;
                    System.out.println("IMClient.getInstance().getCid()"+IMClient.getInstance().getCid());
                    while (true){
                        if (IMClient.getInstance().getCid()!=null){
                            cid=IMClient.getInstance().getCid();
                            break;
                        }
                    }
                    obj.put("userId",Long.parseLong(username));
                    obj.put("regId",cid);
                    System.out.println("login:"+obj.toString());
                    HttpPost post = new HttpPost(Config.LOGIN_URL);
                    HttpResponse httpResponse = null;
                    StringEntity entity = new StringEntity(obj.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    httpResponse = new DefaultHttpClient().execute(post);
                    final int code=httpResponse.getStatusLine().getStatusCode();
                    System.out.println("Status code:"+code);
                    if (code==200){
                        IMClient.getInstance().setCurrentUser(username);
                        IMClient.getInstance().test();
                        IMClient.getInstance().initDB();
                         listen.OnSuccess();
                    }
                    else {
                        listen.OnFalied(code);
                    }

            } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static void FetchNewMsg(String user){
        String path=Config.FETCH_URL+user;
        RequestParams params=new RequestParams();
        params.put("userId",user);
         client.get(path,params,new TextHttpResponseHandler() {
          @Override
          public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    System.out.println("error code:"+i);
          }

          @Override
          public void onSuccess(int i, Header[] headers, String s) {
              try {
                  System.out.println(Thread.currentThread());
                  System.out.println("fetch:"+s);
                  JSONObject object =new JSONObject(s);
                  JSONObject obj=object.getJSONObject("result");
                  JSONArray array =new JSONArray(obj.toString());
                  List<Message> list=new ArrayList<Message>();
                  for (int j=0;j<array.length();j++){
                   Message msg=JSON.parseObject( array.getJSONObject(j).toString(), Message.class);
                    list.add(msg);
                  }
                   IMClient.getInstance().saveMessages(list);
              } catch (JSONException e) {
                  e.printStackTrace();
              }
          }
      });
    }
    private MessageBean Msg2Bean(Message msg) {
        return new MessageBean(msg.getMsgId(), Config.STATUS_SUCCESS, msg.getMsgType(), msg.getContents(), msg.getTimestamp(), msg.getSendType(), null, msg.getSenderId());
    }
}
