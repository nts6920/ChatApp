package com.example.chatapp1.ui.main;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.local.SharedPreferenceHelper;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.databinding.FragmentHomeBinding;
import com.example.chatapp1.ui.adapter.ViewPagerAdapter;
import com.example.chatapp1.ui.base.BaseBindingFragment;
import com.example.chatapp1.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import timber.log.Timber;


public class HomeFragment extends BaseBindingFragment<FragmentHomeBinding, HomeViewModel> {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setUpViewPager(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.viewPager.setOffscreenPageLimit(3);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        binding.bottomNav.getMenu().findItem(R.id.action_message).setChecked(true);
                        break;
                    case 1:
                        binding.bottomNav.getMenu().findItem(R.id.action_contact).setChecked(true);
                        break;
                    case 2:
                        binding.bottomNav.getMenu().findItem(R.id.action_setting).setChecked(true);
                        break;
                }

                Utils.closeKeyboard(getContext(), getView());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_message:
                        binding.viewPager.setCurrentItem(0);
                        break;
                    case R.id.action_contact:
                        binding.viewPager.setCurrentItem(1);
                        break;
                    case R.id.action_setting:
                        binding.viewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected Class<HomeViewModel> getViewModel() {
        return HomeViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }


    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        Timber.e("onCreatedView");
        setUpViewPager();

        mainViewModel.popBackNav.observe(getViewLifecycleOwner(), isGranted -> {
            SharedPreferenceHelper.storeInt(Constant.LOAD_CONTACT_IN_PHONE, 1);

            viewModel.saveLocalContact();
        });

        String color = SharedPreferenceHelper.getStringWithDefault(Constant.backgroundWallpaper, "#FFFFFF");
        boolean isBgColor = SharedPreferenceHelper.getBoolean(Constant.isBackgroundColor, true);

        mainViewModel.isBackgroundColor.postValue(isBgColor);
        mainViewModel.backgroundChat.postValue(color);
    }


    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    protected void onPermissionGranted() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
//        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onStart() {
        super.onStart();

//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
    }
}