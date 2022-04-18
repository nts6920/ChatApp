package com.example.chatapp1.ui.main.settings;

import androidx.lifecycle.MutableLiveData;

import com.example.chatapp1.data.model.Setting;
import com.example.chatapp1.data.repository.SettingRepository;
import com.example.chatapp1.ui.base.BaseViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

@HiltViewModel
public class SettingViewModel extends BaseViewModel {
    public SettingRepository settingRepository;
    public MutableLiveData<List<Setting>> settingList = new MutableLiveData<>();

    @Inject
    public SettingViewModel(SettingRepository settingRepository){
       this.settingRepository = settingRepository;
    }

    public List<Setting> getListSetting(){
        return settingRepository.getListSettings();
    }
}
