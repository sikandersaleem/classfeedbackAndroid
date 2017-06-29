package com.example.classcomplain;


public class messages {

    public String message;
    public String time;
    public String sender;
    public String type;
    public String image;
    public String messagelink;

    public messages(){}

    public messages (String message,String time,String sender, String type, String messagelink,String image) {
        this.message=message;
        this.time=time;
        this.sender=sender;
        this.type = type;
        this.image=image;
        this.messagelink = messagelink;
    }
}
