


package com.example.chatapp1.ui.main.splash;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.chatapp1.R;
import com.example.chatapp1.databinding.FragmentSplashBinding;
import com.example.chatapp1.ui.base.BaseBindingFragment;
import com.example.chatapp1.ui.main.MainActivity;
import com.example.chatapp1.ui.main.MainViewModel;

import timber.log.Timber;

public class SplashFragment extends BaseBindingFragment<FragmentSplashBinding, MainViewModel> {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Class<MainViewModel> getViewModel() {
        return MainViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_splash;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        Window window = requireActivity().getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
        window.setStatusBarColor(Color.TRANSPARENT);

        mainViewModel.popBackNav.observe(getViewLifecycleOwner(), isGranted -> {
            Log.d("popbacknav", isGranted + "");
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                ((MainActivity)requireActivity()).changeMainScreen(R.id.homeFragment);
            }, 1000);
        });

    }

    @Override
    protected void onPermissionGranted() {

    }



    @Override
    public void onDestroy() {

        View decorView = requireActivity().getWindow().getDecorView();
        // Calling setSystemUiVisibility() with a value of 0 clears
        // all flags.
        decorView.setSystemUiVisibility(0);

        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        super.onDestroy();
    }
}