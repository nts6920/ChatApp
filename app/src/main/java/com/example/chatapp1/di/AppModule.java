package com.example.chatapp1.di;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.chatapp1.App;
import com.example.chatapp1.data.local.db.MessageDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {


    @Provides
    @Singleton
    public MessageDatabase getMessageDatabase() {
        return MessageDatabase.getInstance(App.getInstance());
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreference(Application context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
