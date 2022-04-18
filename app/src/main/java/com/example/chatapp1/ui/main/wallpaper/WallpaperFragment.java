package com.example.chatapp1.ui.main.wallpaper;


import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.View;

import com.example.chatapp1.App;
import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.local.SharedPreferenceHelper;
import com.example.chatapp1.databinding.FragmentWallpaperBinding;
import com.example.chatapp1.interfaces.SelectStringListener;
import com.example.chatapp1.interfaces.SelectWallpaperListener;
import com.example.chatapp1.ui.adapter.ColorAdapter;
import com.example.chatapp1.ui.adapter.WallpaperAdapter;
import com.example.chatapp1.ui.base.BaseBindingFragment;
import com.example.chatapp1.ui.main.MainActivity;

import timber.log.Timber;


public class WallpaperFragment extends BaseBindingFragment<FragmentWallpaperBinding, WallpaperViewModel> implements SelectStringListener, SelectWallpaperListener {
    private ColorAdapter colorAdapter;
    private WallpaperAdapter wallpaperAdapter;

    @Override
    protected Class<WallpaperViewModel> getViewModel() {
        return WallpaperViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_wallpaper;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {

        ((SimpleItemAnimator) binding.rvWallpaper.getItemAnimator()).setSupportsChangeAnimations(false);

        mainViewModel.backgroundChat.observe(getViewLifecycleOwner(), s -> {
            mainViewModel.isBackgroundColor.observe(getViewLifecycleOwner(), aBoolean -> {
                if(aBoolean){
                    colorAdapter.setIndexClick(s);
                }else{
                    wallpaperAdapter.setIndexClick(s);
                }
            });
        });

        setUpAdapter();
        setListData();

        binding.ivBackToSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requireActivity() instanceof MainActivity) {
                    requireActivity().onBackPressed();
                }
            }
        });
    }
    private void setUpAdapter(){
        colorAdapter = new ColorAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(App.getInstance(), RecyclerView.HORIZONTAL, false);
        binding.rvColor.setLayoutManager(linearLayoutManager);
        binding.rvColor.setAdapter(colorAdapter);

        binding.rvWallpaper.setLayoutManager(new GridLayoutManager(App.getInstance(), 2));
        wallpaperAdapter = new WallpaperAdapter(this);
        binding.rvWallpaper.setAdapter(wallpaperAdapter);
    }

    private void setListData(){
        if(colorAdapter != null) {
            colorAdapter.setListColor(viewModel.getListColor());
        }

        if(wallpaperAdapter != null){
            wallpaperAdapter.setListWallpaper(viewModel.getListWallpaper());
        }

    }
    @Override
    protected void onPermissionGranted() {

    }

    @Override
    public void onItemStringClick(String color) {
        int position = wallpaperAdapter.getId();
        wallpaperAdapter.setIndexClick(color);
        wallpaperAdapter.notifyItemChanged(position);

        mainViewModel.backgroundChat.postValue(color);
        mainViewModel.isBackgroundColor.postValue(true);

        SharedPreferenceHelper.storeString(Constant.backgroundWallpaper, color);
        SharedPreferenceHelper.storeBoolean(Constant.isBackgroundColor, true);
    }

    @Override
    public void onItemWallpaperClick(String fileImage) {
        int position = colorAdapter.getId();
        colorAdapter.setIndexClick(fileImage);
        colorAdapter.notifyItemChanged(position);

        mainViewModel.backgroundChat.postValue(fileImage);
        mainViewModel.isBackgroundColor.postValue(false);

        SharedPreferenceHelper.storeString(Constant.backgroundWallpaper, fileImage);
        SharedPreferenceHelper.storeBoolean(Constant.isBackgroundColor, false);
    }
}