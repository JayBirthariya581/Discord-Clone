package com.topia.chat.Models;

public class UserEmail {

    private String UID,userName, email,profileImage,password;

    public UserEmail(String UID, String userName, String email, String profileImage, String password) {
        this.UID = UID;
        this.userName = userName;
        this.email = email;
        this.profileImage = profileImage;
        this.password = password;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
