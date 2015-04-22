package com.hedylogos.im;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hedylogos.bean.IMessage;
import com.hedylogos.bean.Message;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;

import java.io.UnsupportedEncodingException;

/**
 * Created by q on 2015/4/17.
 */
public class SendMsgAsyncTask {
    Context c;
    public static final String SEND_URL = "http://hedy.zephyre.me/chats";
    sendTask task;

    public SendMsgAsyncTask(Context c) {
        this.c = c;
    }

    public static void sendMessage(final Context c, IMessage msg) {
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
                    System.out.println("send status code:"+httpResponse.getStatusLine().getStatusCode());


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
}
