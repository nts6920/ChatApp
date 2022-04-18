package com.example.chatapp1.ui.main;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatapp1.common.LiveEvent;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.data.repository.ContactRepository;
import com.example.chatapp1.data.repository.MessageRepository;
import com.example.chatapp1.ui.base.BaseViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

@HiltViewModel
public class MainViewModel extends BaseViewModel {
    public MessageRepository messageRepository;
    public ContactRepository contactRepository;

    public LiveEvent<Message> messReceived= new LiveEvent<>();
    public MutableLiveData<Contact> detailContact= new MutableLiveData<>();
    public MutableLiveData<Boolean> popBackNav = new MutableLiveData<>();
    public MutableLiveData<String> backgroundChat = new MutableLiveData<>();
    public MutableLiveData<Boolean> isBackgroundColor = new MutableLiveData<>();
    public LiveEvent<Contact> contactLiveEvent = new LiveEvent<>();
    public LiveEvent<UserMess> liveUserMess = new LiveEvent<>();


    @Inject
    public MainViewModel(MessageRepository messageRepository, ContactRepository contactRepository) {
        this.messageRepository = messageRepository;
        this.contactRepository = contactRepository;
    }

    public LiveData<List<UserMess>> getMessUser(){
        return messageRepository.getListUserMessInDB();
    }

    public LiveData<List<Contact>> getListContact(){
        return contactRepository.getAllContactInDB();
    }
    public void saveMess(Message message){
        messageRepository.saveMess(message);
    }

    public void saveListContactFromPhone(){
        contactRepository.saveContactFromPhone();
    }

    public void updateUserMessToDB(UserMess userMess){
        messageRepository.updateUserMess(userMess);
    }

    public int checkUserMessExistByPhoneInDB(String phone){
        return messageRepository.isUserMessExistByPhoneName(phone);
    }

    public int checkContactExistByPhoneInDB(String phone){
        return messageRepository.isContactExist(phone);
    }

    public void saveLocalContact(){
        contactRepository.saveLocalContact().subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onComplete() {
                saveLocalMess();
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }
    public void saveLocalMess(){
        messageRepository.saveLocalMess().subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onComplete() {
                saveUserMessList();
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }
    public void getContactByPhone(String phone){
        contactRepository.getContactByPhone(phone).subscribe(new SingleObserver<Contact>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Contact contact) {
                contactLiveEvent.postValue(contact);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }
    public void saveUserMessList(){
        messageRepository.saveUserMessList();
    }


    public void getUserByPhone(String phone){
        messageRepository.getUserByAddress(phone).subscribe(new SingleObserver<UserMess>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull UserMess userMess) {
                liveUserMess.postValue(userMess);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void saveUserMess(UserMess userMess){
        messageRepository.saveUserMess(userMess);
    }
}
