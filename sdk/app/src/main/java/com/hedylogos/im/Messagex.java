package com.hedylogos.im;

public class Messagex {
    private String conversationId;
    private String content;
    private String from;
    private long timestamp;
    private long receiptTimestamp;
    String messageId;
    private int type;

    public Messagex(String conversationId, String from) {
        this(conversationId, from, 0L, 0L);
    }

    public Messagex(String conversationId, String from, long timestamp, long receiptTimestamp) {

        this.conversationId = conversationId;
        this.from = from;
        this.timestamp = timestamp;
        this.receiptTimestamp = receiptTimestamp;
    }

    public String getConversationId() {
        return this.conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom() {
        return this.from;
    }

    protected void setFrom(String from) {
        this.from = from;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getReceiptTimestamp() {
        return this.receiptTimestamp;
    }

    public void setReceiptTimestamp(long receiptTimestamp) {
        this.receiptTimestamp = receiptTimestamp;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getMessagetype() {
        return this.type;
    }

    public void setMessagetype(int type) {
        this.type = type;
    }
}