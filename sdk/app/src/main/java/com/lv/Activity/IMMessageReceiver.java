package com.lv.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.lv.Listener.OnActivityMessageListener;
import com.lv.R;
import com.lv.bean.Message;
import com.lv.im.MessageReceiver;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by q on 2015/4/17.
 */
public class IMMessageReceiver extends MessageReceiver {
    private static final String TAG = "IMMessageReceiver";
    public static ArrayList<MessagerHandler> ehList = new ArrayList<MessagerHandler>();
    public static abstract interface MessagerHandler{
        public void onMsgArrive(Message m);
    }
    @Override
    public void onMessageReceive(Context context, Message msg) {


        for (MessagerHandler handler:ehList){
            handler.onMsgArrive(msg);
        }
        if (ehList.size()==0) {

//            OnActivityMessageListener listener =
//                    sessionMessageDispatchers.get(PrivateConversationActivity.Activityid);
//            msg.setSendType(0);
//
//            System.out.println(msg.toString());
//            if (listener == null) {
//                if (Config.isDebug) {
//                    Log.i(Config.TAG, "Activity inactive, about to send notification. ");
//                }
                NotificationManager nm =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Intent resultIntent = new Intent(context, PrivateConversationActivity.class);
                resultIntent.putExtra(PrivateConversationActivity.DATA_EXTRA_SINGLE_DIALOG_TARGET,
                        JSON.toJSONString(msg));
                resultIntent.putExtra("friend_id", msg.getSenderId());
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                PendingIntent pi =
                        PendingIntent.getActivity(context, -1, resultIntent, PendingIntent.FLAG_ONE_SHOT);
                Notification notification =
                        new NotificationCompat.Builder(context)
                                .setContentTitle("新消息")
                                .setContentText(msg.getContents())
                                .setContentIntent(pi)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setLargeIcon(
                                        BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                                .setAutoCancel(true).build();
                nm.notify(233, notification);
                Log.d(TAG, "notification sent");
//            } else {
//                listener.onMessage(msg);
//            }
        }
    }

    @Override
    public void onMessageReceive(Context context, String msg) {

    }

    @Override
    public void onGetCid(Context context, String cid) {
        System.out.println("cid:"+cid);

    }

    @Override
    public void onGetTHIRDPART_FEEDBACK(Context context, String appid, String taskid, String actionid) {

    }

    public static void registerSessionListener(String peerId, OnActivityMessageListener listener) {
        sessionMessageDispatchers.put(peerId, listener);
    }

    public static void unregisterSessionListener(String peerId) {
        sessionMessageDispatchers.remove(peerId);
    }

    static HashMap<String, OnActivityMessageListener> sessionMessageDispatchers =
            new HashMap<String, OnActivityMessageListener>();
}
