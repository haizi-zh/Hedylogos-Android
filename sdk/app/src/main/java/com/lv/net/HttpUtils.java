package com.lv.net;

import com.lv.Utils.Config;
import com.lv.im.IMClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

/**
 * Created by q on 2015/4/27.
 */
public class HttpUtils {
    public static void login( final String username, final LoginSuccessListen listen) {
        new  Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject obj =new JSONObject();
                try {
                    System.out.println("run");
                    String cid=null;
                           //"45c10d381af0f4998032d933a00f3c6e";
                    System.out.println("IMClient.getInstance().getCid()"+IMClient.getInstance().getCid());
                    while (true){
                        if (IMClient.getInstance().getCid()!=null){
                            cid=IMClient.getInstance().getCid();
                            System.out.println("!!cid");
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
                        System.out.println("denglu"+username);
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
}
