package com.example.chatapp1.ui.main.wallpaper;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

import com.example.chatapp1.data.repository.SettingRepository;
import com.example.chatapp1.ui.base.BaseViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

@HiltViewModel
public class WallpaperViewModel extends BaseViewModel {
    public SettingRepository settingRepository;
    public MutableLiveData<List<String>> colorList = new MutableLiveData<>();
    public MutableLiveData<List<String>> wallpaperList = new MutableLiveData<>();
    @Inject
    public WallpaperViewModel(SettingRepository settingRepository){
        this.settingRepository = settingRepository;
    }

    public List<String> getListColor(){
        return settingRepository.getListColor();
    }

    public List<String> getListWallpaper() {
        return settingRepository.getListWall();
    }
}
