package com.hedylogos.im;

import android.content.Context;

import com.hedylogos.bean.Message;

/**
 * Created by q on 2015/4/17.
 */
public interface MessageListener {
    public abstract void onMessageReceive(Context context, Message msg);

    public abstract void onMessageReceive(Context context, String msg);

    public abstract void onGetCid(Context context, String cid);

    public abstract void onGetTHIRDPART_FEEDBACK(Context context, String appid, String taskid, String actionid);
}
