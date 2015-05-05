package com.lv.im;

import android.os.Handler;

import com.lv.bean.Message;
import com.lv.net.FetchListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by q on 2015/4/25.
 */
public class LazyQueue {
    long max_time = 10000;
    private static LazyQueue instance;
    static HashMap<Long, SortList> LazyMap = new HashMap<Long, SortList>();
    static HashMap<Long, SortList> TempMap = new HashMap<Long, SortList>();
    boolean isRunning;
    DequeueListenr listenr;
    Timer timer;
    private Handler handler;

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

    public void setDequeueListenr(DequeueListenr listenr) {
        this.listenr = listenr;
    }

    public void begin() {
        System.out.println("isRunning " + isRunning);
        if (isRunning) {
            System.out.println("return");
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
    public void setHandler(Handler handler){
        this.handler=handler;
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
            for (int i = 0; i < list.size(); i++) {
                System.out.println("size :"+ list.size());
                try {
                    Message message = list.deleteFirst();
                    if (IMClient.getInstance().isBLOCK()){
                        System.out.println("status: block");
                        System.out.println("dequeue block "+message.getContents());
                     add2Temp(message.getSenderId(), message);
                    }
                    else {
                        if (checkOrder(message)) {
                            System.out.println("dequeue 正序 "+message.getContents());
                            listenr.onDequeueMsg(message);
                        } else {
                            System.out.println("dequeue 乱序 "+message.getContents());
                            add2Temp(message.getSenderId(), message);
                            IMClient.getInstance().setBLOCK(true);
                            IMClient.getInstance().fetchNewMsg(flistener);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Dequeue()");
        //isRunning = false;
    }
    FetchListener flistener=new FetchListener() {
        @Override
        public void OnMsgArrive(List<Message> list) {
            System.out.println("OnMsgArrive");
            for (Message msg:list){
                add2Temp(msg.getSenderId(),msg);
            }
            TempDequeue();
        }
    };
    private void TempDequeue() {
        Iterator iter = TempMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            SortList list = (SortList) entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                try {
                    Message message=list.deleteFirst();
                    System.out.println("list size : "+list.size()+" tempDequeue block "+message.getContents());
                   listenr.onDequeueMsg(message);
//                    android.os.Message m= android.os.Message.obtain();
//                    m.what=1;
//                    m.obj=list.deleteFirst();
//                    handler.sendMessage(m);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("TempDequeue()");
        IMClient.getInstance().setBLOCK(false);
    }
    public void addMsg(long FriendId, Message messageBean) {

        if (!LazyMap.containsKey(FriendId)) {
            LazyMap.put(FriendId, new SortList());
        }
        LazyMap.get(FriendId).insert(messageBean);
        System.out.println("list.insert(messageBean);");
        begin();
    }
    public void add2Temp(long FriendId, Message messageBean) {

        if (!TempMap.containsKey(FriendId)) {
            TempMap.put(FriendId, new SortList());
        }
        TempMap.get(FriendId).insert(messageBean);
        System.out.println("Temp.insert(messageBean);");
    }


    private boolean checkOrder(Message messageBean) {
        int lastid = IMClient.getInstance().getLastMsg(messageBean.getSenderId() + "");
        System.out.println("lastid  " + lastid + " messageBean: " + messageBean.getMsgId());
        if (lastid == -1) {
            IMClient.getInstance().saveMessage(messageBean);
            System.out.println("checkOrder:first msg ");
            return true;
        } else if (messageBean.getMsgId() - 1 == lastid) {
            System.out.println("checkOrder:正序 ");
            IMClient.getInstance().saveMessage(messageBean);
            return true;
        } else {
            System.out.println("checkOrder:乱序 ");
            return false;
        }
    }
}
