package com.example.chatapp1.ui.main.messages;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.data.repository.MessageRepository;
import com.example.chatapp1.ui.base.BaseViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MessageViewModel extends BaseViewModel {
    public MessageRepository messageRepository;
    public MutableLiveData<List<UserMess>> messUserLiveData = new MutableLiveData<>();
    public MutableLiveData<List<UserMess>> userMessSearch = new MutableLiveData<>();

    @Inject
    public MessageViewModel(MessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }

    public LiveData<List<UserMess>> getMessUser(){
        return messageRepository.getListUserMessInDB();
    }

    public LiveData<List<UserMess>> searchMessUser(String s){
        return messageRepository.searchMessUser(s);
    }

    public void deleteUsermessInDB(UserMess userMess){
        messageRepository.deleteUserMess(userMess);
    }
}
