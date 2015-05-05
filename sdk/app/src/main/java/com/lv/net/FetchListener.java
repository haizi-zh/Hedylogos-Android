package com.lv.net;

import com.lv.bean.Message;

import java.util.List;

/**
 * Created by q on 2015/4/30.
 */
public interface FetchListener {
   public void OnMsgArrive(List<Message> list);
}
