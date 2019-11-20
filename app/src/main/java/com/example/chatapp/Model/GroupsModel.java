package com.example.chatapp.Model;

import java.util.List;

public class GroupsModel {

    private String adminid;
    private String groupname;
    private String imageURL;
    private String groupid;


    public GroupsModel(String groupid, String adminid, String groupname, String imageURL, List<User> user) {
        this.adminid = adminid;
        this.groupname = groupname;
        this.imageURL = imageURL;
        this.groupid = groupid;
        this.user = user;
    }

    public String getGroupid(){
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    private List<User> user;

    public GroupsModel() {
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    public String getAdminid() {
        return adminid;
    }

    public void setAdminid(String id) {
        this.adminid = id;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


}
