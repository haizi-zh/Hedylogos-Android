package com.lv.im;

import android.util.Log;

import com.lv.Utils.Config;
import com.lv.bean.Message;

/**
 * Created by q on 2015/4/25.
 */
public class SortList {
  private int size=0;
    private class Data {
        public int value;
        public Message msg;
        private Data next = null;

        Data(Message messageBean) {
            this.value = messageBean.getMsgId();
            this.msg=messageBean;
        }
    }

    private Data first = null;

    public synchronized void insert(Message obj) {
        Data data = new Data(obj);
        size++;
        Data pre = null;
        if (first==null){
            first=data;
            return;
        }

        Data cur = first;
        while (cur!=null) {
            if (data.value==cur.value)return;

            if(data.value>cur.value)
            {
                pre = cur;
                cur = cur.next;}

            else break;
        }
        if (pre == null)
            first = data;
        else
            pre.next = data;
        data.next = cur;
    }

    public synchronized Message deleteFirst(){
        if (first == null) {
            if (Config.isDebug) Log.i(Config.TAG,"first null");
            size--;
            return null;
        }
        Data temp = first;
        first = first.next;
        size--;
        return temp.msg;
    }

    public void display() {
        if (first == null)
            System.out.println("empty");
        System.out.print("first -> last : ");
        Data cur = first;
        while (cur != null) {
            System.out.print(cur.value + " -> ");
            cur = cur.next;
        }
        System.out.print("\n");
    }
public int size(){
    return size;
}
}
