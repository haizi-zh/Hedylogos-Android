package com.lv.bean;

/**
 * Created by q on 2015/4/18.
 */

public class IMessage {
    private int sender;
    private String receiver;
    private int msgType;
    private String contents;

//    public String getConversation() {
//        return conversation;
//    }
//
//    public void setConversation(String conversation) {
//        this.conversation = conversation;
//    }

  //  private String conversation;

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public IMessage(int sender, String receiver, int msgType, String contents) {
        this.sender = sender;
        this.receiver = receiver;
        this.msgType = msgType;
        this.contents = contents;
    }
}