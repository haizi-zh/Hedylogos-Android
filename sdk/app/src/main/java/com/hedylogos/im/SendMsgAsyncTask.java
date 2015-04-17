package com.hedylogos.im;

import android.content.Context;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.hedylogos.bean.Message;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.UnsupportedEncodingException;

/**
 * Created by q on 2015/4/17.
 */
public class SendMsgAsyncTask {
    Context c;
    public static final String SEND_URL = "";
    sendTask task;

    public SendMsgAsyncTask(Context c) {
        this.c = c;
    }

    public static void sendMessage(Message msg) {
        final Object obj = JSON.toJSON(msg).toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpPost post = new HttpPost(SEND_URL);
                HttpResponse httpResponse = null;
                try {
                    StringEntity entity = new StringEntity(obj.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    httpResponse = new DefaultHttpClient().execute(post);
                    System.out.println(EntityUtils.toString(httpResponse
                            .getEntity()));

                } catch (Exception e) {
                }
            }

        }).start();
    }

    class sendTask extends AsyncTask<Message, Void, String> {
        @Override
        protected String doInBackground(Message... params) {
            // JSONObject js=
            Object obj = JSON.toJSON(params).toString();
            // String msg=obj.toString();
            StringEntity entity = null;
            try {
                entity = new StringEntity(obj.toString(),
                        HTTP.UTF_8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(c, SEND_URL, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);

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
