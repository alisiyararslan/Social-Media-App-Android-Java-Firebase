package com.siyararslan.socialmediaapp;

public class Message {
    private String sender;
    private String receiver;
    private String context;
    private String senderImgUri;
    private String receiverImgUri;

    public Message(String sender, String receiver, String context, String senderImgUri, String receiverImgUri) {
        this.sender = sender;
        this.receiver = receiver;
        this.context = context;
        this.senderImgUri = senderImgUri;
        this.receiverImgUri = receiverImgUri;
    }

    public String getSenderImgUri() {
        return senderImgUri;
    }

    public void setSenderImgUri(String senderImgUri) {
        this.senderImgUri = senderImgUri;
    }

    public String getReceiverImgUri() {
        return receiverImgUri;
    }

    public void setReceiverImgUri(String receiverImgUri) {
        this.receiverImgUri = receiverImgUri;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
