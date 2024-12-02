package com.mycreation.astro.object_models;

import java.io.Serializable;

public class NewTaskModel implements Serializable {

    int alarmReqCode;
    boolean repeatingTaskRight;
    boolean dailyRepeatingTaskRight;
    boolean markedToDeleteRight;

    long selectedTimeInMillis;

    String taskTitle;
    String taskDescription;
    String taskImageUrl;
    String taskDocId;

    String schedulerName;
    long scheduledTime;
    boolean weekEndRight;
    boolean highPriority;

    public NewTaskModel() {
    }

    public NewTaskModel(int alarmReqCode, boolean repeatingTaskRight, boolean dailyRepeatingTaskRight, boolean markedToDeleteRight,
                        long selectedTimeInMillis, String taskTitle, String taskDescription, String taskImageUrl,
                        String taskDocId, String schedulerName, long scheduledTime, boolean weekEndRight, boolean highPriority) {

        this.alarmReqCode = alarmReqCode;
        this.repeatingTaskRight = repeatingTaskRight;
        this.dailyRepeatingTaskRight = dailyRepeatingTaskRight;
        this.markedToDeleteRight = markedToDeleteRight;
        this.selectedTimeInMillis=selectedTimeInMillis;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskImageUrl = taskImageUrl;
        this.taskDocId = taskDocId;
        this.schedulerName = schedulerName;
        this.scheduledTime = scheduledTime;
        this.weekEndRight= weekEndRight;
        this.highPriority=highPriority;
    }

    public int getAlarmReqCode() {
        return alarmReqCode;
    }

    public void setAlarmReqCode(int alarmReqCode) {
        this.alarmReqCode = alarmReqCode;
    }

    public boolean isRepeatingTaskRight() {
        return repeatingTaskRight;
    }

    public void setRepeatingTaskRight(boolean repeatingTaskRight) {
        this.repeatingTaskRight = repeatingTaskRight;
    }

    public boolean isDailyRepeatingTaskRight() {
        return dailyRepeatingTaskRight;
    }

    public void setDailyRepeatingTaskRight(boolean dailyRepeatingTaskRight) {
        this.dailyRepeatingTaskRight = dailyRepeatingTaskRight;
    }

    public boolean isMarkedToDeleteRight() {
        return markedToDeleteRight;
    }

    public void setMarkedToDeleteRight(boolean markedToDeleteRight) {
        this.markedToDeleteRight = markedToDeleteRight;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskImageUrl() {
        return taskImageUrl;
    }

    public void setTaskImageUrl(String taskImageUrl) {
        this.taskImageUrl = taskImageUrl;
    }

    public String getTaskDocId() {
        return taskDocId;
    }

    public void setTaskDocId(String taskDocId) {
        this.taskDocId = taskDocId;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public long getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public long getSelectedTimeInMillis() {
        return selectedTimeInMillis;
    }

    public void setSelectedTimeInMillis(long selectedTimeInMillis) {
        this.selectedTimeInMillis = selectedTimeInMillis;
    }

    public boolean isWeekEndRight() {
        return weekEndRight;
    }

    public void setWeekEndRight(boolean weekEndRight) {
        this.weekEndRight = weekEndRight;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }
}
