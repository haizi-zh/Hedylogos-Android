package com.lv.im;

import android.util.Log;

import com.lv.Listener.DequeueListener;
import com.lv.Listener.FetchListener;
import com.lv.Utils.Config;
import com.lv.bean.Message;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by q on 2015/4/25.
 */
public class LazyQueue {
    long max_time = 2000;
    private static LazyQueue instance;
    static HashMap<String, SortList> LazyMap = new HashMap<>();
    static HashMap<String, SortList> TempMap = new HashMap<>();
    boolean isRunning;
    DequeueListener listener;
    Timer timer;

    private LazyQueue() {
//        executeThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                current_time = System.currentTimeMillis();
//                while (true) {
//
//                    if (System.currentTimeMillis() - current_time >= max_time || list.size() >= max_size) {
//                        try {
//                            for (int i = 0; i < list.size(); i++) {
//                                listenr.onDequeueMsg(list.deleteFirst());
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    try {
//                        current_time = System.currentTimeMillis();
//                        Thread.sleep(500);
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        this.executeThread.start();


    }

    public static LazyQueue getInstance() {
        if (instance == null) {
            instance = new LazyQueue();
        }
        return instance;
    }

    public void setDequeueListenr(DequeueListener listener) {
        this.listener = listener;
    }

    public void begin() {
        if (Config.isDebug){
            Log.i(Config.TAG,"isRunning " + isRunning);
        }
        if (isRunning) {
            if (Config.isDebug){
                Log.i(Config.TAG,"return " );
            }
            return;
        }
        isRunning = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isRunning=false;
                new DequeueThread().start();
            }
        }, new Date(System.currentTimeMillis() + max_time));

    }

    class DequeueThread extends Thread{
        @Override
        public void run() {
            Dequeue();
        }
    }

    private void Dequeue() {
        Iterator iter = LazyMap.entrySet().iterator();
        while (iter.hasNext()) {
            System.out.println(Thread.currentThread());
            Map.Entry entry = (Map.Entry) iter.next();
            SortList list = (SortList) entry.getValue();
            //for (int i = 0; i < list.size(); i++) {
                while (list.size()>0){
                    if (Config.isDebug){
                        Log.i(Config.TAG,"size :"+ list.size());
                    }
                try {
                    Message message = list.deleteFirst();
                    if (IMClient.getInstance().isBLOCK()){
                        if (Config.isDebug){
                            Log.i(Config.TAG,"status: block");
                            Log.i(Config.TAG,"dequeue block "+message.getContents());
                        }
                        add2Temp(message.getConversation(), message);
                    }
                    else {
                        if (checkOrder(message)) {
                            if (Config.isDebug){
                                Log.i(Config.TAG,"dequeue 正序 "+message.getContents());
                            }
                            listener.onDequeueMsg(message);
                        } else {
                            if (Config.isDebug){
                                Log.i(Config.TAG,"dequeue 乱序 "+message.getContents());
                            }
                            add2Temp(message.getConversation(), message);
                            IMClient.getInstance().setBLOCK(true);
                            IMClient.getInstance().ackAndFetch(flistener);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (Config.isDebug){
            Log.i(Config.TAG,"Dequeue() ");
        }
    }
    FetchListener flistener=(list) ->{
            for (Message msg:list){
                add2Temp(msg.getConversation(),msg);
            }
            TempDequeue();
    };
    public  void TempDequeue() {
        Iterator iter = TempMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            SortList list = (SortList) entry.getValue();
                while (list.size()>0){
                    Message message=list.deleteFirst();
                    if (message!=null) {
                        if (Config.isDebug) {
                            Log.i(Config.TAG, "list size : " +
                                    "" + list.size() + "" +
                                    " tempDequeue block " + message.getContents());
                        }
                        listener.onDequeueMsg(message);
                    }
            }
        }
        if (Config.isDebug){
            Log.i(Config.TAG,"TempDequeue()");
        }
        IMClient.getInstance().setBLOCK(false);
    }
    public void addMsg(String conversation, Message messageBean) {

        if (!LazyMap.containsKey(conversation)) {
            LazyMap.put(conversation, new SortList());
        }
        LazyMap.get(conversation).insert(messageBean);
        begin();
    }
    public void add2Temp(String conversation, Message messageBean) {

        if (!TempMap.containsKey(conversation)) {
            TempMap.put(conversation, new SortList());
        }
        TempMap.get(conversation).insert(messageBean);
    }
    private boolean checkOrder(Message messageBean) {
        int lastid = IMClient.getInstance().getLastMsg(messageBean.getConversation());
        if (Config.isDebug){
            Log.i(Config.TAG,"lastid  " + lastid + " messageBean: " + messageBean.getMsgId());
        }
        if (lastid == -1) {
            if (Config.isDebug){
                Log.i(Config.TAG,"checkOrder:first msg ");
            }
            return true;
        } else if (messageBean.getMsgId() - 1 == lastid) {
            if (Config.isDebug){
                Log.i(Config.TAG,"checkOrder:正序 ");
            }
            return true;
        } else {
            if (Config.isDebug){
                Log.i(Config.TAG,"checkOrder:乱序 ");
            }
            return false;
        }
    }
}
