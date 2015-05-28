package com.lv.group;

/**
 * Created by q on 2015/5/12.
 */
public interface CreateSuccessListener {
    public void OnSuccess(String groupId,String conversation);
    public void OnFailed();
}
