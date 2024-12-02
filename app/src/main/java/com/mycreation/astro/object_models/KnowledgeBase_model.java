package com.mycreation.astro.object_models;

public class KnowledgeBase_model {

    String apkName;
    String content;
    long timeStamp;
    String editor;
    String docId;

    public KnowledgeBase_model() {
    }

    public KnowledgeBase_model(String apkName, String content, long timeStamp, String editor, String docId) {
        this.apkName = apkName;
        this.content = content;
        this.timeStamp = timeStamp;
        this.editor = editor;
        this.docId = docId;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }


}
