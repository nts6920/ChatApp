package com.example.chatapp1.ui.main.detailcontact;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.data.repository.ContactRepository;
import com.example.chatapp1.data.repository.MessageRepository;
import com.example.chatapp1.ui.base.BaseViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

@HiltViewModel
public class DetailContactViewModel extends BaseViewModel {
    public ContactRepository contactRepository;
    public MessageRepository messageRepository;

    public MutableLiveData<UserMess> userMessMutableLiveData = new MutableLiveData<>();

    @Inject
    public DetailContactViewModel(ContactRepository contactRepository, MessageRepository messageRepository){
        this.contactRepository = contactRepository;
        this.messageRepository = messageRepository;
    }

    public int isUserMessExistByIDContactInDB(int id){
        return contactRepository.isUsermessExistByIDContact(id);
    }

    public void getUserMessByIDContact(int id){
        messageRepository.getUserMessByIDContactInDB(id).subscribe(new SingleObserver<UserMess>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull UserMess userMess) {
                if(userMess != null){
                    userMessMutableLiveData.postValue(userMess);
                }
                else {
                    Log.d("DETAIL_TO_USERMESS", userMess.getContact().getName());
                }
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
