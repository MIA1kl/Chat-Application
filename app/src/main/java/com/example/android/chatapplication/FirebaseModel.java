package com.example.android.chatapplication;

public class FirebaseModel {
    String name;
    String uid;
    String status;

    public FirebaseModel() {
    }

    public FirebaseModel(String name, String uid, String status) {
        this.name = name;
        this.uid = uid;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
