package com.lv.im;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.lv.Listener.DequeueListener;
import com.lv.Listener.MsgListener;
import com.lv.Utils.Config;
import com.lv.bean.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by q on 2015/5/8.
 */
public class HandleImMessage  {
    private static HandleImMessage instance;
    LazyQueue queue=LazyQueue.getInstance();
    Context c;
    private HandleImMessage() {
        MessageReceiver.registerListener(listener,"IM");
        queue.setDequeueListenr(dequeueListener);
    }

    public static HandleImMessage getInstance() {
        if (instance == null) {
            instance = new HandleImMessage();
        }
        return instance;
    }
    public static ArrayList<MessagerHandler> ehList = new ArrayList<MessagerHandler>();
    public static abstract interface MessagerHandler{
        public void onMsgArrive(Message m);
    }
   public static Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message message) {
            switch (message.what){
                case Config.TEXT_MSG:
                    Message newMessage= (Message)message.obj;
                    for (MessagerHandler handler:ehList){
                        handler.onMsgArrive(newMessage);
                    }
                    System.out.println("msg"+newMessage.getContents());
                    break;
                case Config.DOWNLOAD_SUCCESS:
                case Config.DOWNLOAD_FILED:
                    Message newMediaMessage= (Message)message.obj;
                    for (MessagerHandler handler:ehList){
                        handler.onMsgArrive(newMediaMessage);
                    }
                    break;
            }
        }
    };

     MsgListener listener = new MsgListener() {
         @Override
         public void OnMessage(Context context, Message message) {
             c=context;
             queue.addMsg(message.getSenderId(), message);
         }
     };
    DequeueListener dequeueListener=new DequeueListener() {
        @Override
        public void onDequeueMsg(Message messageBean) {
            if (Config.isDebug){
                Log.i(Config.TAG, "onDequeueMsg ");
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
                        c.startService(dlA_intent);
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
                        c.startService(dlI_intent);
                        break;
                }
            }
        }
    };


}
