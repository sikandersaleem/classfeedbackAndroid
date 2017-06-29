package com.example.classcomplain;

import java.util.Date;

public class ChatMessage {

    public String message;
    public String sender;
    public String image;
    public String time;
    public String type;
    public String messagelink;

    public  ChatMessage ()
    {

    }
    public ChatMessage(String message, String sender, String time,String type, String messagelink,String image) {
        this.message = message;
        this.sender = sender;
        this.time = time;
        this.type = type;
        this.image=image;
        this.messagelink = messagelink;
    }
}