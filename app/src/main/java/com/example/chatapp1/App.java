package com.example.chatapp1;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.example.chatapp1.common.Constant;
import com.example.chatapp1.utils.Utils;

import dagger.hilt.android.HiltAndroidApp;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import timber.log.Timber;

@HiltAndroidApp
public class App extends MultiDexApplication {
    private static App instance;
    private String font = "sf_pro_text_default.ttf";
    private String default_font = "SERIF";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        RxJavaPlugins.setErrorHandler(Timber::w);
        initLog();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        font = preferences.getString(Constant.PREF_FONT_CURRENT, font);

        Timber.e("preferenceHelper font: " + font);
        Utils.overrideFont(this, default_font, Constant.FOLDER_FONT+font);


    }
    public void setFont(String font){
        this.font = font;
        Utils.overrideFont(this, default_font, Constant.FOLDER_FONT+font);

    }

    public static App getInstance() {
        return instance;
    }

    private void initLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        LocaleUtils.applyLocale(newBase);
//        super.attachBaseContext(newBase);
//    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
