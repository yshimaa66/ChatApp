package com.example.chatapp.Model;

import java.util.List;

public class GroupChat {

    private String sender;

    private String message;
    private String groupid;


    public GroupChat(String sender, String message, String groupid) {
        this.sender = sender;
        this.message = message;
        this.groupid = groupid;
    }

    public GroupChat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }
}
