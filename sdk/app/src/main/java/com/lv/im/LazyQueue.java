package com.lv.im;

import com.lv.bean.Message;

import java.util.Date;
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
    boolean status;
    DequeueListenr listenr;
    Thread executeThread;
    Timer timer;


    public LazyQueue(final long max_time, final long max_size, final DequeueListenr listenr) {
        this.max_time = max_time;
        this.max_size = max_size;
        this.listenr = listenr;
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

        timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    try {
                        listenr.onDequeueMsg(list.deleteFirst());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        };
        timer.schedule(task,new Date(System.currentTimeMillis()),max_time);
    }
    public void addMsg(Message messageBean) {
        list.insert(messageBean);
    }
}
