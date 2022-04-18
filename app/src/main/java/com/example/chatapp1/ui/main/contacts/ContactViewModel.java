package com.example.chatapp1.ui.main.contacts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.repository.ContactRepository;
import com.example.chatapp1.ui.base.BaseViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

@HiltViewModel
public class ContactViewModel extends BaseViewModel {
    public ContactRepository contactRepository;
    public MutableLiveData<List<Contact>> contactSearch = new MutableLiveData<>();


    @Inject
    public ContactViewModel(ContactRepository contactRepository){
        this.contactRepository = contactRepository;
    }

    public LiveData<List<Contact>> getListContact(){
        return contactRepository.getAllContactInDB();
    }


    public void searchContactList(String phone){
        contactRepository.searchContacts(phone).subscribe(new SingleObserver<List<Contact>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull List<Contact> contacts) {
                contactSearch.postValue(contacts);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

}
