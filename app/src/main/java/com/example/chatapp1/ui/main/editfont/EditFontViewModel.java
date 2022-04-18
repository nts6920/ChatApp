package com.example.chatapp1.ui.main.editfont;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.chatapp1.data.model.Font;
import com.example.chatapp1.data.repository.SettingRepository;
import com.example.chatapp1.ui.base.BaseViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

@HiltViewModel
public class EditFontViewModel extends BaseViewModel {
    public SettingRepository settingRepository;
    public MutableLiveData<List<Font>> fontList = new MutableLiveData<>();

    @Inject
    public EditFontViewModel(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public void getListFont() {
        settingRepository.getListFonts().subscribe(new SingleObserver<List<Font>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(@NonNull List<Font> strings) {
                fontList.postValue(strings);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
//        settingRepository.getListFont(folder, context).subscribe(new SingleObserver<List<Font>>() {
//            @Override
//            public void onSubscribe(@NonNull Disposable d) {
//
//            }
//
//            @Override
//            public void onSuccess(@NonNull List<Font> fonts) {
//                fontList.postValue(fonts);
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//
//            }
//        });
    }

}
