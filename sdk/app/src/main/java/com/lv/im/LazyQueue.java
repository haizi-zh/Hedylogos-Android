package com.lv.im;

import com.lv.bean.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by q on 2015/4/25.
 */
public class LazyQueue {
    long max_time;
    long max_size;
    long current_time;
    static SortList list = new SortList();
    boolean isRunning;
    DequeueListenr listenr;
    Thread executeThread;
    Timer timer;


    public LazyQueue(final long max_time, final long max_size) {
        this.max_time = max_time;
        this.max_size = max_size;
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
    public void setDequeueListenr(DequeueListenr listenr){
        this.listenr=listenr;

    }
    public void start(){

    }
    public void begin(){
      if (isRunning){
          return;
       }
        isRunning=true;
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Dequeue();
                timer.cancel();
                isRunning=false;
            }
        },max_time,max_time);
    }
    private void Dequeue(){
        for (int i = 0; i < list.size(); i++) {
            try {
                listenr.onDequeueMsg(list.deleteFirst());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void addMsg(Message messageBean) {
        list.insert(messageBean);
    }
}
