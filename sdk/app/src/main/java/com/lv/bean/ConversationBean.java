package com.lv.bean;

/**
 * Created by q on 2015/4/21.
 */
public class ConversationBean {
    private int FriendId;
    private long LastChatTime;
    private String HASH;
    private int last_rev_msgId;
    private int isRead; //0:有未读  1：已读

    public ConversationBean(int friendId, long lastChatTime, String HASH, int Last_rev_msgId, int isRead) {
        LastChatTime = lastChatTime;
        this.HASH = HASH;
        FriendId = friendId;
        last_rev_msgId = Last_rev_msgId;
        this.isRead = isRead;
    }

    public int getFriendId() {
        return FriendId;
    }

    public void setFriendId(int friendId) {
        FriendId = friendId;
    }

    public long getLastChatTime() {
        return LastChatTime;
    }

    public void setLastChatTime(long lastChatTime) {
        LastChatTime = lastChatTime;
    }

    public String getHASH() {
        return HASH;
    }

    public void setHASH(String HASH) {
        this.HASH = HASH;
    }

    public int getLast_rev_msgId() {
        return last_rev_msgId;
    }

    public void setLast_rev_msgId(int last_rev_msgId) {
        this.last_rev_msgId = last_rev_msgId;
    }
    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
}
