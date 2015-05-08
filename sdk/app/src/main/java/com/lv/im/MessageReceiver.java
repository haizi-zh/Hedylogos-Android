package com.lv.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.PushConsts;
import com.lv.Listener.MsgListener;
import com.lv.Utils.Config;
import com.lv.Utils.JsonValidator;
import com.lv.bean.Message;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by q on 2015/4/16.
 */
public  class MessageReceiver extends BroadcastReceiver  {

    public static HashMap<String,ArrayList<MsgListener>>routeMap=new HashMap<String,ArrayList<MsgListener>>();

    public static void registerListener(MsgListener listener,String routeKey){
        if (!routeMap.containsKey(routeKey)){
            routeMap.put(routeKey,new ArrayList<MsgListener>());
        }
        routeMap.get(routeKey).add(listener);
    }
    @Override
    public void onReceive( Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        switch (bundle.getInt("action")) {
            case PushConsts.GET_MSG_DATA:
                byte[] payload = bundle.getByteArray("payload");
                String data = null;
                if (payload != null) {
                    data = new String(payload);
                    if (Config.isDebug){
                        Log.i(Config.TAG,"data:"+data);
                    }
                    JsonValidator jsonValidator =new JsonValidator();
                    if (jsonValidator.validate(data)){
                         Message newmsg = JSON.parseObject(data, Message.class);
                            newmsg.setSendType(1);
                        String key="IM";
                        for (MsgListener listener :routeMap.get(key)){
                            listener.OnMessage(context,newmsg);
                        }
                   }
                }
                break;
            case PushConsts.GET_CLIENTID:
                String cid = bundle.getString("clientid");
                IMClient.getInstance().setCid(cid);
                if (Config.isDebug){
                    Log.i(Config.TAG,IMClient.getInstance().getCid());
                }
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                String appid = bundle.getString("appid");
                String task_id = bundle.getString("taskid");
                String actionid = bundle.getString("actionid");
                String result1 = bundle.getString("result");
                long timestamp = bundle.getLong("timestamp");
                Log.d("GetuiSdkDemo", "appid = " + appid);
                Log.d("GetuiSdkDemo", "taskid = " + task_id);
                Log.d("GetuiSdkDemo", "actionid = " + actionid);
                Log.d("GetuiSdkDemo", "result = " + result1);
                Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                break;
            default:
                break;
        }

    }

}
