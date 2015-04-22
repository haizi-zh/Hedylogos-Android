package com.hedylogos.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hedylogos.R;
import com.hedylogos.bean.Message;
import com.hedylogos.im.MessageReceiver;
import com.hedylogos.im.OnActivityMessageListener;

import java.util.HashMap;

/**
 * Created by q on 2015/4/17.
 */
public class IMMessageReceiver extends MessageReceiver {
    private static final String TAG = "IMMessageReceiver";

    @Override
    public void onMessageReceive(Context context, Message msg) {
        OnActivityMessageListener listener =
                sessionMessageDispatchers.get(PrivateConversationActivity.Activityid);
            msg.setType(0);
        if (listener == null) {
            Log.d("IMMessageReceiver", "Activity inactive, about to send notification.");
            NotificationManager nm =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent resultIntent = new Intent(context, PrivateConversationActivity.class);
            resultIntent.putExtra(PrivateConversationActivity.DATA_EXTRA_SINGLE_DIALOG_TARGET,
                    JSON.toJSONString(msg));
           // resultIntent.putExtra(Session.AV_SESSION_INTENT_DATA_KEY, JSON.toJSONString(message));
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
        } else {
            listener.onMessage(msg);
        }
    }

    @Override
    public void onMessageReceive(Context context, String msg) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
//        if (!tasks.isEmpty()) {
//            ComponentName topActivity = tasks.get(0).topActivity;
//            if (!topActivity.getPackageName().equals(context.getPackageName())) {
//
//            }

        OnActivityMessageListener listener =
                sessionMessageDispatchers.get(PrivateConversationActivity.Activityid);

        if (listener == null) {
            Log.d("IMMessageReceiver", "Activity inactive, about to send notification.");
            NotificationManager nm =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            String ctnt = msg;
            Intent resultIntent = new Intent(context, PrivateConversationActivity.class);
            resultIntent.putExtra(PrivateConversationActivity.DATA_EXTRA_SINGLE_DIALOG_TARGET,
                    msg);
            // resultIntent.putExtra(Session.AV_SESSION_INTENT_DATA_KEY, JSON.toJSONString(message));
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

            PendingIntent pi =
                    PendingIntent.getActivity(context, -1, resultIntent, PendingIntent.FLAG_ONE_SHOT);

            Notification notification =
                    new NotificationCompat.Builder(context)
                            .setContentTitle("新消息")
                            .setContentText(ctnt)
                            .setContentIntent(pi)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setLargeIcon(
                                    BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                            .setAutoCancel(true).build();
            nm.notify(233, notification);
            Log.d(TAG, "notification sent");
        } else {
            listener.onMessage(msg);
        }
    }

    @Override
    public void onGetCid(Context context, String cid) {

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
