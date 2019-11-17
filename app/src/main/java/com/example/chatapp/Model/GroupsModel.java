package com.example.chatapp.Model;

public class GroupsModel {

    private String id;
    private String groupname;
    private String imageURL;


    public GroupsModel(String id, String groupname, String imageURL) {
        this.id = id;
        this.groupname = groupname;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
