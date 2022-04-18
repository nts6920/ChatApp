package com.example.chatapp1.data.local.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.data.model.UserMess;

import java.util.List;

@Dao
public interface MessageDAO {
 @Insert(onConflict = OnConflictStrategy.REPLACE)
 void insertUserMess(UserMess userMess);

 @Insert(onConflict = OnConflictStrategy.REPLACE)
 void insertListUserMess(List<UserMess> userMessList);


 @Query("SELECT * FROM UserMess ORDER BY time DESC")
 LiveData<List<UserMess>> getListUserMess();

 @Query("SELECT * FROM UserMess ORDER BY time DESC")
 List<UserMess> getListUserMessInDB();


 @Query("SELECT * FROM UserMess WHERE address LIKE '%' || :s || '%' OR name LIKE '%' || :s || '%'")
 List<UserMess> searchListUserMess(String s);

 @Query("SELECT * FROM UserMess WHERE address LIKE '%' || :s || '%' OR name LIKE '%' || :s || '%'")
 LiveData<List<UserMess>> searchListUserMessInDB(String s);

 @Query("SELECT * FROM UserMess WHERE address =:phone OR phone =:phone")
 UserMess getUserMessByPhone(String phone);

 @Query("SELECT * FROM UserMess WHERE address =:phone")
 UserMess getUserMessByAddress(String phone);

 @Query("SELECT * FROM UserMess WHERE id_contact =:id")
 UserMess getUsermessByIDContact(int id);

 @Update (onConflict = OnConflictStrategy.REPLACE)
 void updateUsermess(UserMess userMess);

 @Delete
 void deleteUsermess(UserMess userMess);

 @Insert(onConflict = OnConflictStrategy.REPLACE)
 void insertMessage(Message sms);

 @Insert(onConflict = OnConflictStrategy.REPLACE)
 void insertListMessages(List<Message> messageList);

 @Query("SELECT * FROM Messages")
 LiveData<List<Message>> getListMessage();

 @Query("SELECT * FROM Messages")
 List<Message> getListMessageInDB();

 @Query("SELECT * FROM Messages WHERE address = :phone ORDER BY time ASC")
 List<Message> getUserMessageByPhone(String phone);

 @Query("SELECT * FROM Messages WHERE address = :phone ORDER BY time ASC")
 LiveData<List<Message>> getMessageByPhone(String phone);

 @Query("SELECT Messages.address FROM Messages")
 List<String> getPhoneInMessage();

 @Insert(onConflict = OnConflictStrategy.REPLACE)
 void insertListUser(List<Contact> contactList);

 @Insert(onConflict = OnConflictStrategy.REPLACE)
 void insertContactUser(Contact contact);

 @Query("SELECT * FROM Contact ORDER BY name")
 LiveData<List<Contact>> getListContactUser();

 @Query("SELECT * FROM Contact ORDER BY name")
 List<Contact> getListContactUserInDB();

 @Query("SELECT * FROM Contact WHERE id_contact=:id")
 Contact getContactById(int id);

 @Query("SELECT name FROM Contact WHERE id_contact=:id")
 String getNameContactById(int id);

 @Query("SELECT * FROM Contact WHERE phone=:phone")
 Contact getUserByPhone(String phone);

 @Query("SELECT * FROM Contact WHERE phone LIKE '%' || :s || '%' OR name LIKE '%' || :s || '%'")
 List<Contact> searchListContact(String s);

 @Update (onConflict = OnConflictStrategy.REPLACE)
 void updateContact(Contact contact);

 @Query("SELECT EXISTS(SELECT * FROM Contact WHERE phone = :phone)")
 Boolean isContactExist(String phone);

 @Query("SELECT EXISTS(SELECT * FROM UserMess WHERE id_contact = :id)")
 Boolean isUsermessExistByID_Contact(int id);

 @Query("SELECT EXISTS(SELECT * FROM UserMess WHERE address=:s OR name =:s)")
 Boolean isUserMessExistByPhoneName(String s);

 @Query("SELECT EXISTS(SELECT * FROM UserMess WHERE address=:s)")
 Boolean isUserMessExistByPhone(String s);
}
