package com.lv.Listener;

import com.lv.bean.Message;

import java.util.List;

/**
 * fetchListener
 *
 * Created by q on 2015/4/30.
 */
public interface FetchListener {
   public void OnMsgArrive(List<Message> list);
}
