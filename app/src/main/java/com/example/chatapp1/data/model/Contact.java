package com.example.chatapp1.data.model;

import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.PipedReader;
import java.io.Serializable;

@Entity(tableName = "Contact")
public class Contact implements Serializable {

    @PrimaryKey
    private int id_contact;

    private String name;

    private String phone;

//    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//    byte[] image;
    private String image;

    public Contact(int id_contact, String name, String phone, String image) {
        this.id_contact = id_contact;
        this.name = name;
        this.phone = phone;
        this.image = image;
    }

    public int getId_contact() {
        return id_contact;
    }

    public void setId_contact(int id_contact) {
        this.id_contact = id_contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
