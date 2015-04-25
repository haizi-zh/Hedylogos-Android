package com.lv.im;

import com.lv.bean.Message;

/**
 * Created by q on 2015/4/25.
 */
public interface DequeueListenr {
    public void onDequeueMsg(Message messageBean);
}
