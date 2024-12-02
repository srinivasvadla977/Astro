package com.mycreation.astro.object_models;

public class ActivityTracker_model {

    long timeStamp;
    String performer;
    int actType;
    String docId;

    public ActivityTracker_model() {
    }

    public ActivityTracker_model(long timeStamp, String performer, int actType, String docId) {
        this.timeStamp = timeStamp;
        this.performer = performer;
        this.actType=actType;
        this.docId=docId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public int getActType() {
        return actType;
    }

    public void setActType(int actType) {
        this.actType = actType;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
