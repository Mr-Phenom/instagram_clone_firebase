package com.company.instagramclone.modelclass;

public class UserModel {
    private String userName;
    private String name;
    private String email;
    private String bio;
    private String imageUrl;
    private String id;

    public UserModel(){

    }
    public UserModel(String userName, String name, String email, String bio, String imageUrl, String id) {
        this.userName = userName;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.imageUrl = imageUrl;
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
