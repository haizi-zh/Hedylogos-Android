package com.lv.Listener;

import com.lv.bean.Message;

/**
 * Created by q on 2015/4/25.
 */
public interface DequeueListener {
    public void onDequeueMsg(Message messageBean);
}
