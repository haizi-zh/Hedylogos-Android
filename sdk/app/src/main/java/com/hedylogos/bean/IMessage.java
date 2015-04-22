package com.hedylogos.bean;

/**
 * Created by q on 2015/4/18.
 */
public class IMessage {
    private long sender;
    private long receiver;
    private String contents;

    public IMessage(long sender, long receiver, String contents) {
        this.sender = sender;
        this.receiver = receiver;
        this.contents = contents;
    }

    public long getSender() {
        return sender;
    }

    public void setSender(long sender) {
        this.sender = sender;
    }

    public long getReceiver() {
        return receiver;
    }

    public void setReceiver(long receiver) {
        this.receiver = receiver;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
