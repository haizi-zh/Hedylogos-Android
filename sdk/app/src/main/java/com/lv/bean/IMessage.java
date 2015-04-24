package com.lv.bean;

/**
 * Created by q on 2015/4/18.
 */

public class IMessage {
    private int sender;
    private int receiver;
    private int msgType;
    private String contents;


    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
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

    public IMessage(int sender, int receiver, int msgType, String contents) {
        this.sender = sender;
        this.receiver = receiver;
        this.msgType = msgType;
        this.contents = contents;

    }
}