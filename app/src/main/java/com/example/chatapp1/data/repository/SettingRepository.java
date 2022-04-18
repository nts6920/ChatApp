package com.example.chatapp1.data.repository;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.chatapp1.App;
import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.model.Font;
import com.example.chatapp1.data.model.Setting;
import com.example.chatapp1.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingRepository {

    @Inject
    public SettingRepository(){
    }

    public Single<List<Font>> getListFonts(){
        return Single.fromCallable(()->getFonts()
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public List<String> getListColor(){
        List<String> list = new ArrayList<>();
        list.add("#FFFFFF");
        list.add("#000000");
        list.add("#EB5757");
        list.add("#F2994A");
        list.add("#F2C94C");
        list.add("#219653");
        list.add("#27AE60");
        list.add("#6FCF97");
        list.add("#007AFF");
        list.add("#2D9CDB");
        list.add("#56CCF2");
        list.add("#9B51E0");

        return list;
    }

    private List<Font> getFonts(){
        List<Font> list = new ArrayList<>();
        list.add(new Font("sf_pro_text_default.ttf", "SF Pro Text (Default)"));
        list.add(new Font("rubik_regular.ttf", "Rubik"));
        list.add(new Font("lato_regular.ttf", "Lato"));
        list.add(new Font("rale_way.ttf", "Raleway"));
        list.add(new Font("notosans_regular.ttf", "Noto Sans"));
        list.add(new Font("sf_pro_text.ttf", "SF Pro Text"));

        return list;
    }

    public List<String> getListWall(){
        List<String> files = null;
        AssetManager assetManager = App.getInstance().getAssets();
        try {
            files = Arrays.asList(assetManager.list(Constant.FOLDER_IMG));
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return files;
    }

    public List<Setting> getListSettings(){
        List<Setting> list = new ArrayList<>();
        list.add(new Setting(App.getInstance().getString(R.string.chat_wallpaper), R.drawable.chat));
        list.add(new Setting(App.getInstance().getString(R.string.font), R.drawable.font));
        list.add(new Setting(App.getInstance().getString(R.string.privacy_policy), R.drawable.privacy_policy));
        list.add(new Setting(App.getInstance().getString(R.string.rate), R.drawable.rate_app));
        list.add(new Setting(App.getInstance().getString(R.string.feedback), R.drawable.feedback));

        return list;
    }

}
