package com.example.chatapp1.data.model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "UserMess")
public class UserMess  implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @Embedded
    private Message message;

    @Embedded
    private Contact contact;

    public UserMess(Message message, Contact contact) {
        this.message = message;
        this.contact = contact;
    }

    @Ignore
    public UserMess( Contact contact) {
        this.contact = contact;
    }

    @Ignore
    public UserMess(Message message) {
        this.message = message;
    }

    @Ignore
    public UserMess() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
