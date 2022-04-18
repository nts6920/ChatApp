package com.example.chatapp1.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.SmsMessage;
import android.text.style.TtsSpan;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatapp1.App;
import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.common.LiveEvent;
import com.example.chatapp1.data.local.db.MessageDatabase;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.data.repository.ContactRepository;
import com.example.chatapp1.data.repository.MessageRepository;
import com.example.chatapp1.service.MessService;
import com.example.chatapp1.ui.main.MainActivity;
import com.example.chatapp1.ui.main.MainViewModel;
import com.klinker.android.logger.Log;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class MyReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    private static final String TAG = "SMS broadcast receiver";
    public String msg, phone;
    public long time;



    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        if(intent.getAction().equals(SMS_RECEIVED)){

            Bundle bundle = intent.getExtras();
            msg = "";
            if(bundle != null){
                Object[] objects = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[objects.length];
                for(int i=0; i<objects.length; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = bundle.getString(context.getResources().getString(R.string.format));
                        messages[i] = SmsMessage.createFromPdu((byte[]) objects[i], format);
                    } else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) objects[i]);
                    }

                    msg += messages[i].getMessageBody();
                    phone = messages[i].getOriginatingAddress();

                    phone = phone.replace("+84", "0");
                    phone = phone.replaceAll("\\s+", "");

                    time = Calendar.getInstance().getTimeInMillis();

                    Message message = new Message(phone, msg, Constant.STATE_NOT_READ_MESS, time, Constant.FOLDER_RECEIVE_MESS);

                    //save list mess
                    List<Message> list = new ArrayList<>();
                    list.add(message);

                    Hawk.init(context).build();

                    if(Hawk.get(Constant.LIST_RECEIVE_MESS) == null){
                        Hawk.put(Constant.LIST_RECEIVE_MESS, list);
                    }
                    else{
                        List<Message> messageList = Hawk.get(Constant.LIST_RECEIVE_MESS);
                        messageList.add(message);
                        Timber.e("RECEIVE_MESS_LIST_SIZE_HAWK: "+messageList.size());
                        Hawk.put(Constant.LIST_RECEIVE_MESS, messageList);
                    }

                    EventBus.getDefault().postSticky(message);
                }
            }
        }
    }

}