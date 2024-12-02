package com.mycreation.astro.object_models;

import java.io.Serializable;

public class User_Model implements Serializable {

    String userName;
    String profilePic;
    String userStatus;
    boolean teamMemberRight;
    String userDeviceId;
    String userDocId;

    public User_Model() {
    }

    public User_Model(String userName, String profilePic, String userStatus, boolean teamMemberRight, String userDeviceId, String userDocId) {
        this.userName = userName;
        this.profilePic = profilePic;
        this.userStatus = userStatus;
        this.teamMemberRight = teamMemberRight;
        this.userDeviceId = userDeviceId;
        this.userDocId = userDocId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public boolean isTeamMemberRight() {
        return teamMemberRight;
    }

    public void setTeamMemberRight(boolean teamMemberRight) {
        this.teamMemberRight = teamMemberRight;
    }

    public String getUserDeviceId() {
        return userDeviceId;
    }

    public void setUserDeviceId(String userDeviceId) {
        this.userDeviceId = userDeviceId;
    }

    public String getUserDocId() {
        return userDocId;
    }

    public void setUserDocId(String userDocId) {
        this.userDocId = userDocId;
    }
}
