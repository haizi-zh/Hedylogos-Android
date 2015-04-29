package com.lv.im;

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
    long max_time=10000;
    long max_size=10;
  // static SortList list = new SortList();
    private static LazyQueue instance;
    static HashMap<Long,SortList>LazyMap =new HashMap<Long, SortList>();
    boolean isRunning;
    DequeueListenr listenr;
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
    public static LazyQueue getInstance(){
        if (instance==null){
            instance=new LazyQueue();
        }
        return instance;
    }
    public void setDequeueListenr(DequeueListenr listenr){
        this.listenr=listenr;

    }
    public void begin(){
        System.out.println("isRunning "+isRunning);
      if (isRunning){
          System.out.println("return");
          return;
       }
        isRunning=true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Dequeue();
                timer.cancel();
            }
        },new Date(System.currentTimeMillis()+max_time));
    }
    private void Dequeue(){
       Iterator iter = LazyMap.entrySet().iterator();
       while (iter.hasNext()) {
           Map.Entry entry = (Map.Entry) iter.next();
           SortList list=(SortList)entry.getValue();
           for (int i = 0; i < list.size(); i++) {
               try {
                   listenr.onDequeueMsg(list.deleteFirst());
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
           }
        System.out.println("Dequeue()");
        isRunning=false;
    }
    public void addMsg(long FriendId,Message messageBean) {

    if (!LazyMap.containsKey(FriendId)){
      LazyMap.put(FriendId,new SortList());
    }
       LazyMap.get(FriendId).insert(messageBean);
        System.out.println("list.insert(messageBean);");
        begin();
    }

}
