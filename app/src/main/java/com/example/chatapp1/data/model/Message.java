package com.example.chatapp1.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Messages")
public class Message implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id_mess;

    private String address;
    private String msg;
    private String readState; //"0" for have not read sms and "1" for have read sms
    private long time;
    private String folderName;

    public Message(String address, String msg, String readState, long time, String folderName) {
        this.address = address;
        this.msg = msg;
        this.readState = readState;
        this.time = time;
        this.folderName = folderName;
    }

    public int getId_mess() {
        return id_mess;
    }

    public void setId_mess(int id_mess) {
        this.id_mess = id_mess;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReadState() {
        return readState;
    }

    public void setReadState(String readState) {
        this.readState = readState;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
