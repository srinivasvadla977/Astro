package com.mycreation.astro.object_models;

public class GroupChat_model {
    String name;
    String message;
    String imageUrl;
    long timeStamp;
    String docId;
    String profilePic;
    String likes;
    String deviceId;

    public GroupChat_model() {
    }

    public GroupChat_model(String name, String message, String imageUrl, long timeStamp, String docId, String profilePic, String likes, String deviceId) {
        this.name = name;
        this.message = message;
        this.imageUrl = imageUrl;
        this.timeStamp = timeStamp;
        this.docId = docId;
        this.profilePic=profilePic;
        this.likes=likes;
        this.deviceId=deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}
