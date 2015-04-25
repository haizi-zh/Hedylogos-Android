package com.lv.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.lv.Utils.JsonValidator;
import com.lv.bean.Message;

/**
 * Created by q on 2015/4/16.
 */
public abstract class MessageReceiver extends BroadcastReceiver implements MessageListener {
LazyQueue queue;
    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
        System.out.println(" messageid:" + bundle.getString("messageid"));
        System.out.println("bundle.getInt(action):" + bundle.getInt("action"));
        switch (bundle.getInt("action")) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                Log.d("GetuiSdkDemo", " messageid:" + messageid);
                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
                String data = null;
                if (payload != null) {
                    data = new String(payload);
                    System.out.println("data:"+data);
                    JsonValidator jsonValidator =new JsonValidator();
                    if (jsonValidator.validate(data)){
                        final Message newmsg = JSON.parseObject(data, Message.class);
                        newmsg.setSendType(1);
                        queue = new LazyQueue(10000,10,new DequeueListenr() {
                            @Override
                            public void onDequeueMsg(Message messageBean) {

                                onMessageReceive(context, messageBean);
                                System.out.println("dequeue调用");
                            }
                        });
                         queue.addMsg(newmsg);
                    }
                  else {
                        System.out.println("非json格式");
                        onMessageReceive(context, data);
                    }
                }
                break;
            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                onGetCid(context, cid);
                Log.d("GetuiSdkDemo", "cid:" + cid);
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                String appid = bundle.getString("appid");
                String task_id = bundle.getString("taskid");
                String actionid = bundle.getString("actionid");
                String result1 = bundle.getString("result");
                long timestamp = bundle.getLong("timestamp");
                onGetTHIRDPART_FEEDBACK(context, appid, task_id, actionid);
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
