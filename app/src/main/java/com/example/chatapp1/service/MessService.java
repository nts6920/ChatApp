package com.example.chatapp1.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.chatapp1.App;
import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.local.db.MessageDatabase;
import com.example.chatapp1.data.model.Message;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

public class MessService extends Service {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private MessageDatabase messageDatabase;

    private List<Message> messageList = new ArrayList<>();

    public List<Message> getReceiveMess(){
        return messageList;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

     //   registerReceiver(myReceiver, new IntentFilter(SMS_RECEIVED));

    }

    private void sendMessToActivity(String msg){
        Intent intent = new Intent("receiveMessFromMessService");
        intent.putExtra("service_send_mess", msg);
        sendBroadcast(intent);

        Timber.e("sendMessToActivity" + msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.e( "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}