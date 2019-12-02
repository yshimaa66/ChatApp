package com.example.chatapp.Model;

import java.util.List;

public class GroupChat {

    private String sender;

    private String message;
    private String groupid;

    private String time;

    List<String> isseen;

    boolean isseenall;

    public GroupChat(String sender, String message, String groupid, String time, List<String> isseen,boolean isseenall) {
        this.sender = sender;
        this.message = message;
        this.groupid = groupid;
        this.time = time;
        this.isseen = isseen;
        this.isseenall=isseenall;
    }

    public boolean isIsseenall() {
        return isseenall;
    }

    public void setIsseenall(boolean isseenall) {
        this.isseenall = isseenall;
    }

    public List<String> getIsseen() {
        return isseen;
    }

    public void setIsseen(List<String> isseen) {
        this.isseen = isseen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
