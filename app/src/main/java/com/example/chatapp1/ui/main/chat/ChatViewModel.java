package com.example.chatapp1.ui.main.chat;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatapp1.common.LiveEvent;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.data.repository.ContactRepository;
import com.example.chatapp1.data.repository.MessageRepository;
import com.example.chatapp1.ui.base.BaseViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

@HiltViewModel
public class ChatViewModel extends BaseViewModel {
    public MessageRepository messageRepository;
    public ContactRepository contactRepository;
    public LiveEvent<UserMess> liveUserMess = new LiveEvent<>();

    @Inject
    public ChatViewModel(MessageRepository messageRepository, ContactRepository contactRepository){
        this.messageRepository = messageRepository;
        this.contactRepository = contactRepository;
    }

    public LiveData<List<Message>> getUserMessByPhone(String phone){
        return messageRepository.getListMessByPhoneInDB(phone);
    }

    public void saveMessToDB(Message message){
        messageRepository.saveMess(message);
    }

    public void deleteUserMessInDB(UserMess userMess){
        messageRepository.deleteUserMess(userMess);
    }

    public void updateUserMessToDB(UserMess userMess) {
        messageRepository.updateUserMess(userMess);
    }

    public int checkUserMessExistByPhoneInDB(String phone){
        return messageRepository.isUserMessExistByPhoneName(phone);
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

    public void saveUserMess(UserMess userMess) {
        messageRepository.saveUserMess(userMess);
    }
}
