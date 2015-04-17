package com.hedylogos.im;

import com.hedylogos.bean.Message;

/**
 * Created by q on 2015/4/17.
 */
public interface OnActivityMessageListener {
    public void onMessage(Message msg);

    public void onMessage(String msg);
}
