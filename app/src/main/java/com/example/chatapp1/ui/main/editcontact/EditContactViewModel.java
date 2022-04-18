package com.example.chatapp1.ui.main.editcontact;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

import com.example.chatapp1.data.model.Contact;
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
public class EditContactViewModel extends BaseViewModel {
    public ContactRepository contactRepository;
    public MessageRepository messageRepository;

    public MutableLiveData<UserMess> userMessEditContactMutableLiveData = new MutableLiveData<>();

    @Inject
    public EditContactViewModel(ContactRepository contactRepository, MessageRepository messageRepository){
        this.contactRepository = contactRepository;
        this.messageRepository = messageRepository;
    }

    public void updateContact(Contact contact){
        contactRepository.updateContact(contact);
    }

    public void updateUserMess(UserMess userMess){
        messageRepository.updateUserMess(userMess);
    }
    public int isUserMessExistByIDContactInDB(int id) {
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
                userMessEditContactMutableLiveData.postValue(userMess);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void saveUserMess(UserMess userMess) {
        messageRepository.saveUserMess(userMess);
    }

    public void insertImageContact(int id, Bitmap bit){
        contactRepository.insertImageContact(id, bit);
    }

    public void updateNameContact(int id, String name){
        contactRepository.updateNameContact(id, name);
    }

    public void updatePhoneContact(int id, String phone){
        contactRepository.updatePhoneContact(id, phone);
    }

    public void updateImageContact(int id, Bitmap bitmap){
        contactRepository.updateImageContact(id, bitmap);
    }
}
