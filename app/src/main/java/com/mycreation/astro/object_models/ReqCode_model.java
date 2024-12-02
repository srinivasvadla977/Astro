package com.mycreation.astro.object_models;

public class ReqCode_model {

    int token;
    String docId;

    public ReqCode_model() {
    }

    public ReqCode_model(int token, String docId) {
        this.token = token;
        this.docId = docId;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
