package com.example.chatapp1.ui.main.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatapp1.App;
import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.local.SharedPreferenceHelper;
import com.example.chatapp1.data.model.Setting;
import com.example.chatapp1.databinding.FragmentSettingBinding;
import com.example.chatapp1.interfaces.SelectSettingListener;
import com.example.chatapp1.ui.adapter.SettingAdapter;
import com.example.chatapp1.ui.base.BaseBindingFragment;
import com.example.chatapp1.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import company.librate.RateDialog;
import company.librate.SelectRateApp;

public class SettingFragment extends BaseBindingFragment<FragmentSettingBinding, SettingViewModel> implements SelectSettingListener, SelectRateApp {
    private SettingAdapter settingAdapter;

    private List<Setting> list = new ArrayList<>();
    private int index =  SharedPreferenceHelper.getInt(Constant.CHECK_RATE_APP);

    @Override
    protected Class getViewModel() {
        return SettingViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        setupAdapter();

        list = viewModel.getListSetting();

        if(settingAdapter != null){
            settingAdapter.setSettingList(list);
        }

        if(index == Constant.RATED_APP){
            settingAdapter.removeRate();
        }
    }

    private void setupAdapter() {
        settingAdapter = new SettingAdapter(this, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvSettings.setLayoutManager(layoutManager);
        binding.rvSettings.setAdapter(settingAdapter);
    }

        @Override
    protected void onPermissionGranted() {

    }


    @Override
    public void onItemSettingClick(Setting setting) {
        String name = setting.getName();
        if(name.equals(App.getInstance().getString(R.string.chat_wallpaper))){
          Navigation.findNavController(binding.getRoot()).navigate(R.id.action_homeFragment_to_wallpaperFragment);
        }
        if(name.equals(App.getInstance().getString(R.string.font))){
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_homeFragment_to_editFontFragment);
        }
        if(name.equals(App.getInstance().getString(R.string.privacy_policy))){
            linkToPolicy();
        }
        if(name.equals(App.getInstance().getString(R.string.rate))){
            showRateApp(getContext(), Constant.LINK_SHARE_APP);
        }
        if(name.equals(App.getInstance().getString(R.string.feedback))){
            Utils.sendEmail(requireContext(), Constant.EMAIL);
        }
    }



    private void linkToPolicy(){
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL_POLICY));
//        startActivity(intent);

        String url = Constant.URL_POLICY;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setPackage(Constant.PACKAGE_TO_POLICY);
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            // Chrome is probably not installed
            // Try with the default browser
            i.setPackage(null);
            startActivity(i);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(int isClick) {
        if(isClick == Constant.CLICK){
            Toast.makeText(requireContext(), R.string.rate_app, Toast.LENGTH_SHORT).show();
            SharedPreferenceHelper.storeInt(Constant.CHECK_RATE_APP, Constant.RATED_APP);
            settingAdapter.removeRate();
        }
    }

    private void showRateApp(Context context, String supportEmail) {
        RateDialog rateDialog = new RateDialog(context, supportEmail, true, this);
        Window window = rateDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
//        wlp.verticalMargin = 0.1f;
        //  wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;

        window.setAttributes(wlp);
        rateDialog.show();
    }
}