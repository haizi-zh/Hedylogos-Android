package com.lv.im;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lv.bean.IMessage;
import com.lv.bean.Message;
import com.lv.bean.MessageBean;
import com.lv.data.MessageDB;

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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by q on 2015/4/17.
 */
public class SendMsgAsyncTask {

    Context c;
    public static final String SEND_URL = "http://hedy.zephyre.me/chats";
    public static final String ACK_URL = "http://hedy.zephyre.me/chats";
    sendTask task;
    public SendMsgAsyncTask(Context c) {
        this.c = c;
    }
public static void sendMsg(Context c, IMessage msg){
    String str = JSON.toJSON(msg).toString();
    try {
        StringEntity entity = new StringEntity(str,
                HTTP.UTF_8);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params=new RequestParams();
      //  params.put("ContentType","application/json");

        params.put("sender",msg.getSender());
        params.put("contents",msg.getContents());
        params.put("msgType",msg.getMsgType());
        params.put("receiver",msg.getReceiver());
        client.post(c,SEND_URL,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                System.out.println("aaaastatusCode:"+statusCode);
            }
        });







    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    }

}
    public static void sendMessage(final String correntUser, final String currentFri, final IMessage msg) {
        final String str = JSON.toJSON(msg).toString();
        System.out.println("send_message:"+str);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpPost post = new HttpPost(SEND_URL);
                HttpResponse httpResponse = null;
                try {
                  StringEntity entity = new StringEntity(str,
                           HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    httpResponse = new DefaultHttpClient().execute(post);
                    int code= httpResponse.getStatusLine().getStatusCode();
                    System.out.println("send status code:"+code);
                    if (code==200){
                        HttpEntity res=httpResponse.getEntity();
                        String result=EntityUtils.toString(res);
                        System.out.println(result);
                        JSONObject object =new JSONObject();
                        String a  = object.get("result").toString();
                        JSONObject obj =new JSONObject();
                        String conversation=object.get("conversation").toString();
                        String msgId=object.get("msgId").toString();
                        Long timestamp=Long.parseLong(object.get("timestamp").toString());
                        MessageDB db= new MessageDB(correntUser);
                        db.saveMsg(currentFri,new MessageBean(Integer.parseInt(msgId),0,msg.getMsgType(),msg.getContents(),timestamp,0,null,Long.parseLong(correntUser)));
                    }
                    else {
                        System.out.println("发送失败：code "+code);
                    }

                  //  db.updateMsg(currentFri, ,msgId,conversation,timestamp);

//                    AsyncHttpClient client = new AsyncHttpClient();
//                    client.post(c, SEND_URL, entity, "application/json", new JsonHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                            super.onSuccess(statusCode, headers, response);
//                            System.out.println("statusCode:"+statusCode);
//                            if (statusCode == 200) {
//                                Log.i("IMSDK","发送成功！");
//                            }
//                        }
//                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }
    public void sendMsg(Message msg) {
        task = new sendTask();
        task.execute(msg);
    }
    class sendTask extends AsyncTask<Message, Void, String> {
        @Override
        protected String doInBackground(Message... params) {
            String str = JSON.toJSON(params[0]).toString();
            StringEntity entity = null;
            try {
                entity = new StringEntity(str,
                        HTTP.UTF_8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(c, SEND_URL, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    if (statusCode == 200) {
                        Log.i("IMSDK","发送成功！");
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

    }
    public static void postack(final ArrayList<String> list,String id){
        final String url =ACK_URL+id+"/ack";
          final JSONObject obj =new JSONObject();
        try {
            obj.put("msgList",JSON.toJSONString(list));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
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
                    System.out.println("send status code:"+httpResponse.getStatusLine().getStatusCode());
                    HttpEntity res=httpResponse.getEntity();
                    System.out.println(EntityUtils.toString(res));
                    System.out.println("list size:"+list.size());
                    list.clear();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();

    }
}
