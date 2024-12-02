package com.mycreation.astro.object_models;

public class LeaveTracker_model {

    long timeStamp;
    String personsOnLeave;
    String docId;

    public LeaveTracker_model() {
    }

    public LeaveTracker_model(long timeStamp, String personsOnLeave, String docId) {
        this.timeStamp = timeStamp;
        this.personsOnLeave = personsOnLeave;
        this.docId = docId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPersonsOnLeave() {
        return personsOnLeave;
    }

    public void setPersonsOnLeave(String personsOnLeave) {
        this.personsOnLeave = personsOnLeave;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
