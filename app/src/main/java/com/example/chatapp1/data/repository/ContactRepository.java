package com.example.chatapp1.data.repository;


import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;

import com.example.chatapp1.App;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.local.SharedPreferenceHelper;
import com.example.chatapp1.data.local.db.MessageDatabase;
import com.example.chatapp1.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class ContactRepository {
  //  MessageDatabase messageDatabase;
    SharedPreferenceHelper sharedPreferenceHelper;

    @Inject
    public ContactRepository(SharedPreferenceHelper sharedPreferenceHelper){
        this.sharedPreferenceHelper = sharedPreferenceHelper;
    }

    public void saveContactFromPhone(){
        Completable.fromRunnable(() -> {
            try {
                saveListContactInPhone();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }


    public Completable saveLocalContact(){
        return Completable.fromAction(()->{
            if(SharedPreferenceHelper.getBoolean(Constant.LOAD_ALL_Contact_FIRST_TIME, true)) {
                saveContactToDB(getAllListContact(App.getInstance()));
                SharedPreferenceHelper.storeBoolean(Constant.LOAD_ALL_Contact_FIRST_TIME, false);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Contact>> searchContacts(String s){
        return Single.fromCallable(()->{
            return MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().searchListContact(s);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Contact> getContactById(int id){
        return Single.fromCallable(()-> getContactByIdInDB(id)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
    public Single<Contact> getContactByPhone(String phone){
        return Single.fromCallable(()-> getContactByPhoneInDB(phone)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public void updateContact(Contact contact){
        Completable.fromRunnable(() -> {
            updateContactInDB(contact);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private void saveListContactInPhone() throws IOException {
        ArrayList<Contact> contactList1 = (ArrayList<Contact>) MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getListContactUserInDB();
        ArrayList<Contact> contactList2 = (ArrayList<Contact>) getAllListContact(App.getInstance());
        ArrayList<Contact> contactList = new ArrayList<>();

        Timber.e("GET_CONTACT_IN_PHONE: "+ contactList1.size() +"---"+contactList2.size());

        if(contactList2.size() > contactList1.size()){
            for(Contact contact : contactList2){
                boolean found = false;
                for(Contact contact1 : contactList1){
                    if(contact.getId_contact() == contact1.getId_contact()){
                        found = true;
                    }
                }
                if(!found){
                    contactList.add(contact);
                }
            }

            Timber.e("CONTACT_ADD_LIST_SIZE: "+contactList.size());

            for(int i=0; i<contactList.size(); i++){
                MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().insertContactUser(contactList.get(i));
                Timber.e("ADD_CONTACT_IN_PHONE: "+contactList.get(i).getName());
            }
        }
        else{
            Timber.e("CONTACT_FULL_SAVED");
        }
    }



    private List<Contact> getAllListContact(Context context) {
        List<Contact> contactList = new ArrayList<>();

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "ASC";
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";

                Cursor cursorPhone = context.getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);
                if (cursorPhone.moveToNext()) {
                    String number = cursorPhone.getString(cursorPhone.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    number = number.replace("+84", "0");
                    number = number.replaceAll("\\s+","");

                    Uri imageUri = Utils.getContactPhoto(Long.parseLong(id));
                    InputStream is = null;
                    Contact contact;

                    try {
                        is = context.getContentResolver().openInputStream(imageUri);
                    }catch (Exception e){
                    }

                    if(is==null){
                        //no image
                        contact = new Contact(Integer.parseInt(id),name, number,null);
                    }
                    else{
                        contact = new Contact(Integer.parseInt(id),name, number, Utils.getContactPhoto(Long.parseLong(id)).toString());

                    }

                    contactList.add(contact);

                }
                cursorPhone.close();
            }
            cursor.close();
        }
        return contactList;
    }

    private int isContactExist(String phone){
        if(MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().isContactExist(phone))
            return 1;
        return 0;
    }
    public int isUsermessExistByIDContact(int id){
        if(MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().isUsermessExistByID_Contact(id))
            return 1;
        return 0;
    }

    private void updateContactInDB(Contact contact){
        MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().updateContact(contact);
    }

    private void insertListContactInDB(List<Contact> contacts){
        MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().insertListUser(contacts);
    }
    public LiveData<List<Contact>> getAllContactInDB(){
        return MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getListContactUser();
    }

    private Contact getContactByIdInDB(int id){
        return MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getContactById(id);
    }

    private Contact getContactByPhoneInDB(String phone){
        return MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().getUserByPhone(phone);
    }
    private void saveContactToDB(List<Contact> contacts){
        MessageDatabase.getInstance(App.getInstance().getApplicationContext()).getMessageDAO().insertListUser(contacts);
    }



    public void insertImageContact(int id, Bitmap bit){
        Completable.fromRunnable(() -> {
            insertImageBitmap(id, bit);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private void insertImageBitmap(int id, Bitmap bit){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] photo = stream.toByteArray();

        ContentValues values = new ContentValues();

        values.put(ContactsContract.Data.RAW_CONTACT_ID, id);
        values.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1);
        values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, photo);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);

        App.getInstance().getApplicationContext().getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }

    public void updateImageContact(int id, Bitmap bitmap){
        Completable.fromRunnable(() -> {
            updateImageBitmap(id, bitmap);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private void updateImageBitmap(int contactId, Bitmap bitmap) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if(bitmap != null){
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[]{String.valueOf(contactId), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO,stream.toByteArray())
                    .build());
        }
        // Update
        try {
            App.getInstance().getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateNameContact(int id, String name){
        Completable.fromRunnable(() -> {
            updateName(id, name);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private void updateName(int id, String name) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[]{String.valueOf(id), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, "")
                .build());

        // Update
        try {
            App.getInstance().getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePhoneContact(int id, String phone){
        Completable.fromRunnable(() -> {
            updatePhone(id, phone);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private void updatePhone(int id, String phone){
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[]{String.valueOf(id), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .build());

        // Update
        try {
            App.getInstance().getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        }
        catch (Exception e) {
            e.printStackTrace();
            Timber.e("EDIT CONTACT ERROR");
        }
    }

}
