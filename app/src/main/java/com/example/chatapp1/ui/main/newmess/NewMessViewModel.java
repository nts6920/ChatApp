package com.example.chatapp1.ui.main.newmess;

import androidx.lifecycle.MutableLiveData;

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
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

@HiltViewModel
public class NewMessViewModel extends BaseViewModel {
    public MessageRepository messageRepository;
    public ContactRepository contactRepository;
    public MutableLiveData<List<UserMess>> userMessListLiveData = new MutableLiveData<>();
    public MutableLiveData<UserMess> userMessLiveData = new MutableLiveData<>();
    public MutableLiveData<Contact> contactMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<List<UserMess>> userMessList = new MutableLiveData<>();

    public MutableLiveData<Integer> isUserExist = new MutableLiveData<>();
    public MutableLiveData<Integer> isContactExist = new MutableLiveData<>();

    @Inject
    public NewMessViewModel(MessageRepository messageRepository, ContactRepository contactRepository) {
        this.messageRepository = messageRepository;
        this.contactRepository = contactRepository;
    }


    public void saveMessToDB(Message message) {
        messageRepository.saveMess(message);
    }

    public void saveUserMess(UserMess userMess) {
        messageRepository.saveUserMess(userMess);
    }


    public void getUsersToSendMess(String s) {
        messageRepository.getListUserByNamePhone(s).subscribe(new SingleObserver<List<UserMess>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull List<UserMess> userMesses) {
                userMessListLiveData.postValue(userMesses);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void getUsersInDB() {
        messageRepository.getAllUserInDB().subscribe(new SingleObserver<List<UserMess>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull List<UserMess> userMesses) {
                userMessList.postValue(userMesses);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public int checkContactExistByPhone(String phone) {
       return messageRepository.isContactExist(phone);
    }

    public int checkMessUserExistByPhoneInDB(String phone) {
        return messageRepository.isUserMessExistByPhoneName(phone);
    }

    public void getUserByPhone(String phone) {
        messageRepository.getUserByPhone(phone).subscribe(new SingleObserver<UserMess>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull UserMess userMess) {
                userMessLiveData.postValue(userMess);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void getContactByPhone(String phone) {
        contactRepository.getContactByPhone(phone).subscribe(new SingleObserver<Contact>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull Contact contact) {
                contactMutableLiveData.postValue(contact);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void updateUserMessToDB(UserMess userMess) {
        messageRepository.updateUserMess(userMess);
    }
}
