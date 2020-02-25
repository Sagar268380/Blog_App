package com.example.blogapp2.models;

import com.google.firebase.database.ServerValue;

public class Comments {
    String content,uid,uimg,uname;
    Object time,timeStamp;

    public Comments(String content, String uid, String uimg, String uname) {
        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.timeStamp = ServerValue.TIMESTAMP;

    }

    public Comments(String content, String uid, String uimg, String uname, Object time, Object timeStamp) {

        this.content = content;
        this.uid = uid;
        this.uimg = uimg;
        this.uname = uname;
        this.time = time;
        this.timeStamp =timeStamp;
    }

    public Comments() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUimg() {
        return uimg;
    }

    public void setUimg(String uimg) {
        this.uimg = uimg;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public Object getTime() {
        return time;
    }

    public void setTime(Object time) {
        this.time = time;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
