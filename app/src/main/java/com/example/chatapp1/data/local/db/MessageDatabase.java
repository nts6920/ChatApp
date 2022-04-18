package com.example.chatapp1.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.data.model.UserMess;

@Database(entities = {Message.class, Contact.class, UserMess.class} , version = 1)
public abstract class MessageDatabase extends RoomDatabase {
    public static MessageDatabase instance;
    public static synchronized MessageDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), MessageDatabase.class, Constant.DB_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
    public abstract MessageDAO getMessageDAO();
}
