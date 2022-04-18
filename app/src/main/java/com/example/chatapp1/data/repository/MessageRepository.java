package com.example.chatapp1.data.repository;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.chatapp1.App;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.data.local.SharedPreferenceHelper;
import com.example.chatapp1.data.local.db.MessageDatabase;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class MessageRepository {
//    MessageDatabase messageDatabase;
//    SharedPreferenceHelper sharedPreferenceHelper;

    private static SharedPreferenceHelper sharedPreferenceHelper;
   // private static MessageDatabase messageDatabase;

    private final MediatorLiveData<Message> mData = new MediatorLiveData<>();

    @Inject
    public MessageRepository(SharedPreferenceHelper sharedPreferenceHelper){
        this.sharedPreferenceHelper = sharedPreferenceHelper;
    }

    public Completable saveLocalMess(){
        return Completable.fromAction(()->{
            if(SharedPreferenceHelper.getBoolean(Constant.LOAD_ALL_SMS_FIRST_TIME, true)) {
                saveLocalMessageToDB(getAllListMess());
                SharedPreferenceHelper.storeBoolean(Constant.LOAD_ALL_SMS_FIRST_TIME, false);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void saveUserMessList(){
        Completable.fromRunnable(() -> {
            if(SharedPreferenceHelper.getBoolean(Constant.LOAD_ALL_UserMess_FIRST_TIME, true)) {
                saveUserMessToDB(getListMessageUser());
                SharedPreferenceHelper.storeBoolean(Constant.LOAD_ALL_UserMess_FIRST_TIME, false);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public void saveUserMess(UserMess userMess){
        Completable.fromRunnable(() -> {
            MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().insertUserMess(userMess);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public void saveMess(Message message){
        Completable.fromRunnable(() -> {
            MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().insertMessage(message);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public void saveListMessToDB(List<Message> list){
        Completable.fromRunnable(() -> {
            MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().insertListMessages(list);
        }).subscribeOn(Schedulers.io()).subscribe();
    }


    public void deleteUserMess(UserMess userMess){
        Completable.fromRunnable(() -> {
            MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().deleteUsermess(userMess);
        }).subscribeOn(Schedulers.io()).subscribe();
    }


    public void updateUserMess(UserMess userMess){
        Completable.fromRunnable(() -> {
            MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().updateUsermess(userMess);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public LiveData<List<UserMess>> searchMessUser(String s){
        return MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().searchListUserMessInDB(s);
    }

    public Single<List<UserMess>> getListUserByNamePhone(String s){
        return Single.fromCallable(()->{
            return getListUserByNamePhoneInDB(s);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<UserMess> getUserByPhone(String phone){
        return Single.fromCallable(()->{
            return MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getUserMessByPhone(phone);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<UserMess> getUserByAddress(String address){
        return Single.fromCallable(()-> MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getUserMessByAddress(address)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<UserMess> getUserMessByIDContactInDB(int id){
        return Single.fromCallable(()->{
            return MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getUsermessByIDContact(id);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<List<UserMess>> getListUserMessInDB(){
        return MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getListUserMess();
    }

    public Single<List<UserMess>> getAllUserInDB(){
        return Single.fromCallable(()->{
            return MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getListUserMessInDB();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private List<UserMess> getListUserByNamePhoneInDB(String s){
        List<UserMess> list = new ArrayList<>();
        List<Contact> contactList = MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().searchListContact(s);
        List<UserMess> userMessList = MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().searchListUserMess(s);

        for (int i=0; i<contactList.size(); i++){
            Contact contact = contactList.get(i);
            String phone = contact.getPhone();
            if(!MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().isUserMessExistByPhoneName(phone)){
                UserMess userMess = new UserMess(contact);
                list.add(userMess);
            }
        }
        for(int i=0; i<userMessList.size(); i++){
            list.add(userMessList.get(i));
        }
        return list;
    }

    private List<UserMess> getListMessageUser(){
        List<UserMess> userMessList = new ArrayList<>();
        List<String> listPhone = getPhonesFromMessage();
        List<Message> listMess = new ArrayList<>();

        for(int i=0; i<listPhone.size(); i++){
            listMess = MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getUserMessageByPhone(listPhone.get(i));
            Message lastMess = listMess.get(listMess.size() - 1);
            Contact contact = MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getUserByPhone(listPhone.get(i));

            if(contact != null){
                UserMess userMess = new UserMess(lastMess, contact);
                userMessList.add(userMess);
            }
            else{
                UserMess userMess = new UserMess(lastMess);
                userMessList.add(userMess);
            }
        }

        Log.d("GET_MESS_USER", userMessList.size()+"");

        return userMessList;
    }

    private List<String> getPhonesFromMessage(){
        List<String>  listPhone = MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getPhoneInMessage();
        listPhone = new ArrayList<String>(new LinkedHashSet<String>(listPhone));
        return listPhone;
    }

    public Single<List<Message>> getMessReceiveFromPhone(){
        return Single.fromCallable(this::getNewMessReceiveFromPhone).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private List<Message> getNewMessReceiveFromPhone(){
        ArrayList<Message> messages1 = (ArrayList<Message>) MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getListMessageInDB();
        ArrayList<Message> messages2 = (ArrayList<Message>) getAllListMess();
        List<Message> messages = new ArrayList<>();

        Timber.e("GET_MESS_IN_PHONE: "+ messages1.size() +"---"+messages2.size());

        if(messages2.size() > messages1.size()){
            Timber.e("ADD_MESS_FROM_PHONE");
            for(int i=0; i<messages2.size(); i++){
                if(!messages1.contains(messages2.get(i))){
                    messages.add(messages2.get(i));
//                    messageDatabase.getMessageDAO().insertMessage(messages2.get(i));
                }
            }
        }
        else{
            Timber.e("MESSAGE_FULL_SAVED");
        }

        return messages;
    }

    private List<Message> getAllListMess() {
        List<Message> list = new ArrayList<>();

        ContentResolver cr = App.getInstance().getContentResolver();
        Cursor c = cr.query(Uri.parse("content://sms"), null, null, null, null);
        int totalSMS = 0;
        if (c != null) {
            totalSMS = c.getCount();
            if (c.moveToFirst()) {
                for (int j = 0; j < totalSMS; j++) {
                    String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.DATE));
                    String state = c.getString(c.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.READ));
                    String address = c.getString(c.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.ADDRESS));
                    address = address.replace("+84", "0");
                    address = address.replaceAll("\\s+","");
                    String body = c.getString(c.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.BODY));
                    String type = c.getString(c.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.TYPE));

                    Message sms = new Message(address, body, state, Long.parseLong(smsDate), type);
                    list.add(sms);
                    c.moveToNext();
                }
            }
            c.close();
        }
        Log.d("GET_SMS_FROM_PHONE", list.size()+"");
        return list;
    }

    public LiveData<List<Message>> getListMessByPhoneInDB(String phone){
        return MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getMessageByPhone(phone);
    }

    private void saveUserMessToDB(List<UserMess> list){
        MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().insertListUserMess(list);
    }


    private LiveData<List<Message>> getAllMessageInDB(){
        return MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getListMessage();
    }

    private void saveLocalMessageToDB(List<Message> messages){
        MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().insertListMessages(messages);
    }

    public int isUserMessExistByPhoneName(String s){
        if(MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().isUserMessExistByPhoneName(s))
            return Constant.EXIST;
        return Constant.NO_EXIST;
    }

    public int isUserMessExistByPhone(String s){
        if(MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().isUserMessExistByPhone(s))
            return Constant.EXIST;
        return Constant.NO_EXIST;
    }

    public int isContactExist(String phone){
        if(MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().isContactExist(phone))
            return Constant.EXIST;
        return Constant.NO_EXIST;
    }
}
