package com.hedylogos.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hedylogos.Activity.MainActivity;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

/**
 * Created by q on 2015/4/16.
 */
public class MessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
        System.out.println(" messageid:" + bundle.getString("messageid"));
        System.out.println("bundle.getInt(action):"+bundle.getInt("action"));
        switch (bundle.getInt("action")) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                String msg = bundle.getString("message");
                Log.d("GetuiSdkDemo", " msg:" +  msg);
                Log.d("GetuiSdkDemo", " messageid:" +  messageid);
                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
                String data = null;
                if (payload != null) {
                    data = new String(payload);
                    Log.d("GetuiSdkDemo", "Got Payload:" + data);
                   if (MainActivity.btn != null)
                       MainActivity.btn.setText(data);
                    //start(bundle, context);
                    //sendResult(data);
                }
                break;
            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                Log.d("GetuiSdkDemo", "cid:" +cid);
               // if (GetuiSdkDemoActivity.tView != null)
                //    GetuiSdkDemoActivity.tView.setText(cid);
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                String appid = bundle.getString("appid");
                String taskid1 = bundle.getString("taskid");
                String actionid = bundle.getString("actionid");
                String result1 = bundle.getString("result");
                long timestamp = bundle.getLong("timestamp");

                Log.d("GetuiSdkDemo", "appid = " + appid);
                Log.d("GetuiSdkDemo", "taskid = " + taskid1);
                Log.d("GetuiSdkDemo", "actionid = " + actionid);
                Log.d("GetuiSdkDemo", "result = " + result1);
                Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                break;
            default:
                break;
        }
    }
}
