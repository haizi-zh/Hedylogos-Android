package com.lv.user;

import android.util.Log;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.lv.Utils.Config;
import com.lv.group.CreateSuccessListener;
import com.lv.im.IMClient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by q on 2015/5/11.
 */
public class HttpManager {
    private static SyncHttpClient client = new SyncHttpClient();
    static ExecutorService exec = Executors.newFixedThreadPool(5);

    public static void login(final String username, final LoginSuccessListener listen) {

        exec.execute(()->{
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
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "login:" + obj.toString());
                    }
                    HttpPost post = new HttpPost(Config.LOGIN_URL);
                    HttpResponse httpResponse = null;
                    StringEntity entity = new StringEntity(obj.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
                    defaultHttpClient.getParams().setParameter("Timeout", 5 * 1000);
                    httpResponse = defaultHttpClient.execute(post);
                    final int code = httpResponse.getStatusLine().getStatusCode();
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "Status code:" + code);
                    }
                    if (code == 200) {
                        User.getUser().setCurrentUser(username);
                        UserDao.getInstance();
                        IMClient.getInstance().initDB();
                        listen.OnSuccess();
                    } else {
                        listen.OnFailed(code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
    }

    public static void createGroup(String name, String groupType, boolean isPublic, String avatar, List<Long> participants, final long row, final CreateSuccessListener listener) {
        final JSONObject obj = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            for (long member:participants){
                array.put(member);
            }
            obj.put("name", name);
            obj.put("groupType", groupType);
            obj.put("isPublic", isPublic);
            obj.put("avatar", avatar);
            obj.put("participants",array);
            System.out.println(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        exec.execute(()->{
                HttpPost post = new HttpPost(Config.HOST + "/groups");
                post.addHeader("UserId",User.getUser().getCurrentUser()+ "");
                HttpResponse httpResponse = null;
                try {
                    System.out.println(obj.toString());
                    StringEntity entity = new StringEntity(obj.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    httpResponse = new DefaultHttpClient().execute(post);
                    System.out.println("create status code:" + httpResponse.getStatusLine().getStatusCode());
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity res = httpResponse.getEntity();
                        String result= EntityUtils.toString(res);
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "create group Result : " +result );
                        }
                        JSONObject object=new JSONObject(result);
                        JSONObject jsonObject=object.getJSONObject("result");
                        String groupId=jsonObject.getString("groupId");
                        String conversation=jsonObject.getString("conversation");
                        IMClient.getInstance().addGroup2Conversation(groupId,conversation);
                        UserDao.getInstance().updateGroup(row,groupId,conversation);
                        if (Config.isDebug){
                            Log.i(Config.TAG,"群组更新成功");
                        }
                        listener.OnSuccess(groupId,conversation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
    }

    public static void addMembers(String groupId, List<Long> members, boolean isPublic) {
        final JSONObject obj = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            array.put(members.get(0));
            //array.put(3);
            obj.put("action", "addMembers");
            obj.put("isPublic", isPublic);
            obj.put("participants", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editGroupMembers(groupId, obj);
    }

    public static void removeMembers(String groupId, List<Long> members, boolean isPublic) {
        final JSONObject obj = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            array.put(100001);
            //array.put(3);
            obj.put("action", "delMembers");
            obj.put("isPublic", isPublic);
            obj.put("participants", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editGroupMembers(900078 + "", obj);
    }

    public static void silenceMembers(String groupId, List<Long> members, boolean isPublic) {
        final JSONObject obj = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            array.put(100001);
            //array.put(3);
            obj.put("action", "silence");
            obj.put("isPublic", isPublic);
            obj.put("participants", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editGroupMembers(900078 + "", obj);
    }

    public static void editGroupMembers(final String GroupId, final JSONObject obj) {

        exec.execute(()->{
                HttpPost post = new HttpPost(Config.HOST + "/groups/" + GroupId);
                post.addHeader("UserId", 100002 + "");
                HttpResponse httpResponse = null;
                try {
                    System.out.println(obj.toString());
                    StringEntity entity = new StringEntity(obj.toString(),
                            HTTP.UTF_8);
                    entity.setContentType("application/json");
                    post.setEntity(entity);
                    httpResponse = new DefaultHttpClient().execute(post);
                    System.out.println("create status code:" + httpResponse.getStatusLine().getStatusCode());
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity res = httpResponse.getEntity();
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "edit member Result : " + EntityUtils.toString(res));
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
    }

    public static void getGroupMembers(String groupId) {
        String url = Config.GET_GROUP + groupId + "/users";
        getInformations(url, "");
    }

    public static void getGroupInformation(String groupId) {
        final String url = Config.GET_GROUP + groupId;
        getInformations(url, "");
    }

    private static void getInformations(final String url, String type) {
        exec.execute(()->{
                HttpGet get = new HttpGet(url);
                get.addHeader("UserId", User.getUser().getCurrentUser() + "");
                try {
                    HttpResponse httpResponse = new DefaultHttpClient().execute(get);
                    HttpEntity res = httpResponse.getEntity();
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "group Info : " + EntityUtils.toString(res));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });
    }

    public static void getUserGroupInfo(String userId) {
        final String url = Config.HOST + "/users/100001/groups";
        exec.execute(()->{
                HttpGet get = new HttpGet(url);
                get.addHeader("UserId", 100001 + "");
                try {
                    HttpResponse httpResponse = new DefaultHttpClient().execute(get);
                    System.out.println("code " + httpResponse.getStatusLine().getStatusCode());
                    HttpEntity res = httpResponse.getEntity();
                    if (Config.isDebug) {
                        Log.i(Config.TAG, "User-Group Info : " + EntityUtils.toString(res));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });
    }

    public static void searchGroup(final String tag, final String value) {
        final String url = Config.HOST + "/groups/search";

        exec.execute(()->{
                RequestParams params = new RequestParams();
                params.put(tag, value);
               // params.add("UserId",100001+"");
                client.addHeader("UserId",100001l+"");
                client.get(url, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        if (Config.isDebug) {
                            throwable.printStackTrace();
                            Log.i(Config.TAG, i + " fail  " + s);
                        }
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        if (Config.isDebug) {
                            Log.i(Config.TAG, i + "  " + s);
                        }
                    }
                });
        });
    }
}

