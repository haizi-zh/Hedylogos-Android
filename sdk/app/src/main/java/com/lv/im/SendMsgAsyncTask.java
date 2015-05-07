package com.lv.im;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lv.Listener.SendMsgListener;
import com.lv.Utils.Config;
import com.lv.bean.IMessage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by q on 2015/4/17.
 */
public class SendMsgAsyncTask {

    Context c;
    sendTask task;

    public SendMsgAsyncTask(Context c) {
        this.c = c;
    }

    public static void sendMessage(final String correntUser, final String currentFri, final IMessage msg, final long localId, final SendMsgListener listen) {
        final String str = JSON.toJSON(msg).toString();
        System.out.println("send_message:" + str);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpPost post = new HttpPost(Config.SEND_URL);
                HttpResponse httpResponse = null;
                try {
                    StringEntity entity = new StringEntity(str,
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    httpResponse = new DefaultHttpClient().execute(post);
                    int code = httpResponse.getStatusLine().getStatusCode();
                    if (Config.isDebug){
                        Log.i(Config.TAG, "send status code:" + code);
                    }
                    if (code == 200) {
                        HttpEntity res = httpResponse.getEntity();
                        String result = EntityUtils.toString(res);
                        if (Config.isDebug){
                            Log.i(Config.TAG,result);
                        }
                        JSONObject object = new JSONObject(result);
                        JSONObject obj = object.getJSONObject("result");
                        String conversation = obj.get("conversation").toString();
                        String msgId = obj.get("msgId").toString();
                        Long timestamp = Long.parseLong(obj.get("timestamp").toString());
                        IMClient.getInstance().setLastMsg(currentFri, Integer.parseInt(msgId));
                        IMClient.getInstance().updateMessage(currentFri, localId, msgId, conversation, timestamp, Config.STATUS_SUCCESS);
                        if (Config.isDebug){
                            Log.i(Config.TAG,"发送成功，消息更新！");
                        }
                        listen.onSuccess();
                    } else {
                        if (Config.isDebug){
                            Log.i(Config.TAG,"发送失败：code " + code);
                        }
                        listen.onFailed(code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public void sendMsg(String correntUser, String currentFri, IMessage msg, long localId, SendMsgListener listen) {
        task = new sendTask();
        task.execute(correntUser, currentFri, msg, localId, listen);
    }

    class sendTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... params) {
            String correntUser = (String) params[0];
            String currentFri = (String) params[1];
            IMessage msg = (IMessage) params[2];
            long localId = (Long) params[3];
            SendMsgListener listen = (SendMsgListener) params[4];
            String str = JSON.toJSON(msg).toString();
            HttpPost post = new HttpPost(Config.SEND_URL);
            HttpResponse httpResponse = null;
            try {
                StringEntity entity = new StringEntity(str,
                        HTTP.UTF_8);
                entity.setContentType("application/json");
                post.setEntity(entity);
                httpResponse = new DefaultHttpClient().execute(post);
                int code = httpResponse.getStatusLine().getStatusCode();
                if (Config.isDebug){
                    Log.i(Config.TAG,"send status code:" + code);
                }
                if (code == 200) {
                    HttpEntity res = httpResponse.getEntity();
                    String result = EntityUtils.toString(res);
                    System.out.println(result);
                    JSONObject object = new JSONObject(result);
                    JSONObject obj = object.getJSONObject("result");
                    String conversation = obj.get("conversation").toString();
                    String msgId = obj.get("msgId").toString();
                    Long timestamp = Long.parseLong(obj.get("timestamp").toString());
                    IMClient.getInstance().setLastMsg(currentFri, Integer.parseInt(msgId));
                    IMClient.getInstance().updateMessage(currentFri, localId, msgId, conversation, timestamp, Config.STATUS_SUCCESS);
                    if (Config.isDebug){
                        Log.i(Config.TAG,"发送成功，消息更新！");
                    }
                    listen.onSuccess();
                } else {
                    if (Config.isDebug){
                        Log.i(Config.TAG,"发送失败：code " + code);
                    }
                    listen.onFailed(code);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}


