package com.lv.im;

import com.lv.bean.MessageBean;


/**
 * Created by q on 2015/4/25.
 */
public class test {
    public static void main(String[] args){
        SortedList myqueue=new SortedList();
        myqueue.insert(new MessageBean("121"),2);
        myqueue.insert(new MessageBean("2"),3);
        myqueue.insert(new MessageBean("4"),7);
        myqueue.insert(new MessageBean("5"),1);
        myqueue.insert(new MessageBean("6"),5);
       myqueue.display();





    }

    public static class SortedList {
        private class Data {
            private MessageBean obj;
            private int value;
            private Data next = null;

            Data(MessageBean obj) {
                this.obj = obj;
                this.value = obj.getServerId();

            }
        }

        private Data first = null;

        public void insert(MessageBean obj,int value) {
            Data data = new Data(obj);
            data.obj.setServerId(value);
            Data pre = null;
            if (first==null){
                first=data;
                return;
            }

            Data cur = first;
            while (data.value
                    >cur.value) {

                pre = cur;
                cur = cur.next;
                System.out.println("pre:"+pre.value+" cur:"+cur.value);
            }
            if (pre == null)
                first = data;
            else
                pre.next = data;
            data.next = cur;
        }

        public Object deleteFirst() throws Exception {
            if (first == null)
                throw new Exception("empty!");
            Data temp = first;
            first = first.next;
            return temp.obj;
        }

        public void display() {
            if (first == null)
                System.out.println("empty");
            System.out.print("first -> last : ");
            Data cur = first;
            while (cur != null) {
                System.out.print(cur.obj.getServerId() + " -> ");
                cur = cur.next;
            }
            System.out.print("\n");
        }

    }
}
