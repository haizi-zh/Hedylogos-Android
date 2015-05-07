package com.lv.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.PushConsts;
import com.lv.Listener.DequeueListener;
import com.lv.Listener.MessageListener;
import com.lv.Utils.Config;
import com.lv.Utils.JsonValidator;
import com.lv.bean.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;


/**
 * Created by q on 2015/4/16.
 */
public abstract class MessageReceiver extends BroadcastReceiver implements MessageListener {
LazyQueue queue=LazyQueue.getInstance();
    private  WeakReference<Context> contextWeakReference;
    DequeueListener listenr;
    Context c;
    MsgHandler handler = new MsgHandler(MessageReceiver.this);
     class MsgHandler extends Handler {
        WeakReference<BroadcastReceiver> mReceiveReference;
        MsgHandler(BroadcastReceiver receiver) {
            mReceiveReference= new WeakReference<BroadcastReceiver>(receiver);
        }

        @Override
        public void handleMessage(android.os.Message message) {
            final BroadcastReceiver receiver = mReceiveReference.get();
            if (receiver != null) {
                switch (message.what){
                    case Config.TEXT_MSG:
                        Message newMessage= (Message)message.obj;
                        onMessageReceive(c,newMessage);
                        break;
                    case Config.DOWNLOAD_SUCCESS:
                    case Config.DOWNLOAD_FILED:
                        Message newMediaMessage= (Message)message.obj;
                        onMessageReceive(c,newMediaMessage);
                        break;
                }
            }
        }
    }
    @Override
    public void onReceive(final Context context, Intent intent) {
        if ("DOWNLOAD_COMPLETE".equals(intent.getAction())){
            if (Config.isDebug){
                Log.i(Config.TAG,"newMediaMsg");
            }
            Message message=(Message)intent.getSerializableExtra("newMsg");
            onMessageReceive(context,message);
        }
        contextWeakReference=new WeakReference<Context>(context);
        Bundle bundle = intent.getExtras();
        c=context;
        System.out.println("bundle.getInt(action):" + bundle.getInt("action"));
        switch (bundle.getInt("action")) {
            case PushConsts.GET_MSG_DATA:
                byte[] payload = bundle.getByteArray("payload");
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                //boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
               // System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
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
                            queue.addMsg(newmsg.getSenderId(), newmsg);
                   }
                }
                break;
            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                String cid = bundle.getString("clientid");
                IMClient.getInstance().setCid(cid);
                if (Config.isDebug){
                    Log.i(Config.TAG,IMClient.getInstance().getCid());
                }
                onGetCid(context, cid);
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
        listenr=new DequeueListener() {
            @Override
            public void onDequeueMsg(Message messageBean) {
                if (Config.isDebug){
                    Log.i(Config.TAG,"onDequeueMsg " );
                }
                messageBean.setSendType(1);
                int result =IMClient.getInstance().saveReceiveMsg(messageBean);
                if (Config.isDebug){
                    Log.i(Config.TAG,"result :"+result);
                }
                if (result==0){
                    String content=messageBean.getContents();
                    JSONObject object=null;
                    switch (messageBean.getMsgType()){
                        case Config.TEXT_MSG:
                            android.os.Message handlermsg= android.os.Message.obtain();
                            handlermsg.obj=messageBean;
                            handlermsg.what= Config.TEXT_MSG;
                            handler.sendMessage(handlermsg);
                            break;
                        case Config.AUDIO_MSG:
                            try {
                                object =new JSONObject(content);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent dlA_intent=new Intent("ACTION.IMSDK.STARTDOWNLOAD");
                            String aurl=null;
                            try {
                                aurl =object.getString("url");
                                System.out.println("url "+aurl);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            messageBean.setUrl(aurl);
                            dlA_intent.putExtra("msg",messageBean);
                            context.startService(dlA_intent);
                            break;
                        case Config.IMAGE_MSG:
                            try {
                                object =new JSONObject(content);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String iurl=null;
                            try {
                                iurl =object.getString("thumb");
                                if (Config.isDebug){
                                    Log.i(Config.TAG,"url " + iurl);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            messageBean.setUrl(iurl);
                            Intent dlI_intent=new Intent("ACTION.IMSDK.STARTDOWNLOAD");
                            dlI_intent.putExtra("msg",messageBean);
                            context.startService(dlI_intent);
                            break;
                    }
                }
            }
        };
        queue.setDequeueListenr(listenr);
    }

}
