package com.hedylogos.bean;

/**
 * Created by q on 2015/4/21.
 */
public class ConversationBean {
   private int FriendId;
   private long LastChatTime;
   private String HASH;

    public ConversationBean( int friendId,long lastChatTime, String HASH) {
        LastChatTime = lastChatTime;
        this.HASH = HASH;
        FriendId = friendId;
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
}
