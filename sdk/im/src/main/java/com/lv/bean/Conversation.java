package com.lv.bean;

public class Conversation {
    private String lastMessage;
    private long lastTime;
    private int readState;
    private int friendId;
    private String conversation;
    private String chatType;

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }


    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public int getReadState() {
        return readState;
    }

    public void setReadState(int readState) {
        this.readState = readState;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Conversation(String lastMessage, long lastTime, int friendId, int state,String conversation,String chatType ) {
        this.lastMessage = lastMessage;
        this.lastTime = lastTime;
        this.friendId = friendId;
        this.state = state;
        this.conversation=conversation;
        this.chatType=chatType;

    }

    private int state;

}