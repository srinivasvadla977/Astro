package com.mycreation.astro.object_models;

public class LikedItemIndex_model {

    String docId;
    int index;

    public LikedItemIndex_model() {
    }

    public LikedItemIndex_model(String docId, int index) {
        this.docId = docId;
        this.index = index;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
