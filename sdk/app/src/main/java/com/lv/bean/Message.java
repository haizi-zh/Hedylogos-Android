package com.lv.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table MESSAGE.
 */
public class Message {
    /**
     id
     string
     conversation
     string	所属会话的ID
     msgId
     int
     msgType
     int	1: 普通消息
     senderId
     long	发送者ID
     senderAvatar
     string	发送者头像
     senderName
     string	发送者名称
     contents
     string	消息内容
     timestamp
     long	时间戳
     int
     sendType 0 发送 1 接受
     **/
    private String id;
    private String conversation;
    private int msgId;
    private int msgType;
    private long senderId;
    private String senderAvatar;
    private String senderName;
    private String contents;
    private long timestamp;
    private int sendType;

    public Message (){
    }

    public Message(String id, String conversation, int msgId, int msgType, long senderId, String senderAvatar, String senderName, String contents, long timestamp, int sendType) {
        this.id = id;
        this.conversation = conversation;
        this.msgId = msgId;
        this.msgType = msgType;
        this.senderId = senderId;
        this.senderAvatar = senderAvatar;
        this.senderName = senderName;
        this.contents = contents;
        this.timestamp = timestamp;
        this.sendType = sendType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversation() {
        return conversation;
    }

    public void setConversation(String conversation) {
        this.conversation = conversation;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSendType() {
        return sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }
}

