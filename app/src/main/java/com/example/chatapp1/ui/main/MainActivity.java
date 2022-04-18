package com.example.chatapp1.ui.main;


import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.provider.Settings;
import android.telephony.SmsMessage;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.chatapp1.App;
import com.example.chatapp1.common.Constant;

import com.example.chatapp1.R;
import com.example.chatapp1.data.local.db.MessageDatabase;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.data.repository.MessageRepository;
import com.example.chatapp1.databinding.ActivityMainBinding;
import com.example.chatapp1.service.MessService;
import com.example.chatapp1.ui.base.BaseBindingActivity;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class MainActivity extends BaseBindingActivity<ActivityMainBinding, MainViewModel> {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private List<String> permissionDenied = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    public NavController navControllerMain;
    public NavHostFragment navHostFragmentMain;
    private NavGraph graph;


    private String[] PERMISSIONS = new String[]{Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Message message) {
        //receiveMess(message);

        if(Hawk.get(Constant.LIST_RECEIVE_MESS) != null){
            List<Message> list = Hawk.get(Constant.LIST_RECEIVE_MESS);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    // this will run in the main thread
                    for(int i=0; i<list.size(); i++){
                        receiveMess(list.get(i));
                    }
                    Hawk.delete(Constant.LIST_RECEIVE_MESS);

                }
            });
        }
    }

    private void receiveMess(Message message) {
        viewModel.saveMess(message);

        String phone = message.getAddress();

        //check phone in usermess
        int i = viewModel.checkUserMessExistByPhoneInDB(phone);
        if(i == Constant.EXIST){
            //have phone in usermess
            viewModel.getUserByPhone(phone);
            viewModel.liveUserMess.observe(this, userMess -> {
                Timber.e("USERMESS");
                if (userMess instanceof UserMess){
                    Timber.e("HAVE PHONE IN USERMESS");
                    ((UserMess) userMess).setMessage(message);
                    viewModel.updateUserMessToDB(((UserMess) userMess));
                }
            });
        }
        else{
            int isContactExist = viewModel.checkContactExistByPhoneInDB(phone);
            if(isContactExist == Constant.EXIST){
                viewModel.getContactByPhone(phone);
                viewModel.contactLiveEvent.observe(this, contact -> {
                    Timber.e("HAVE PHONE IN CONTACT");
                    if(contact instanceof Contact){
                        UserMess userMess = new UserMess(message, (Contact) contact);
                        viewModel.saveUserMess(userMess);
                    }
                });
            }
            else{
                Timber.e("DON'T HAVE PHONE");
                UserMess userMess = new UserMess(message);
                viewModel.saveUserMess(userMess);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MessageDatabase.getInstance(getApplicationContext());

        Hawk.init(getApplicationContext()).build();

        navHostFragmentMain = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        if (navHostFragmentMain != null) {
            navControllerMain = navHostFragmentMain.getNavController();
        }

        checkAndRequestPermission();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

       EventBus.getDefault().unregister(this);

        Timber.e("onDestroy");
    }

    @Override
    protected void onResume() {
        Timber.e("onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.e("onPause");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public Class<MainViewModel> getViewModel() {
        return MainViewModel.class;
    }

    @Override
    public void setupView(Bundle savedInstanceState) {

    }

    @Override
    public void setupData() {

    }

    public void changeMainScreen(int idScreen) {
        if (graph == null) {
            graph = navControllerMain.getNavInflater().inflate(R.navigation.main_nav);
        }
        graph.setStartDestination(idScreen);
        navControllerMain.setGraph(graph);
    }

    private void checkAndRequestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            List<String> unGrantedPermissions = new ArrayList<>();
            for (String permission: PERMISSIONS) {
                if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                    unGrantedPermissions.add(permission);
                }
            }
            if(unGrantedPermissions.size() != 0){
                Object[] unGranted1 = unGrantedPermissions.toArray();
                String[] unGranted2 = Arrays.copyOf(unGranted1, unGrantedPermissions.size(), String[].class);
                requestPermissions(unGranted2, Constant.REQUEST_PERMISSION);
            } else {
                viewModel.popBackNav.postValue(true);
            }
        } else {
            viewModel.popBackNav.postValue(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            List<String> unGrantedPermissions = new ArrayList<>();
            for(int i = 0; i < permissions.length; i++){
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    unGrantedPermissions.add(permissions[i]);
                }
            }
            if(unGrantedPermissions.size() == 0){
                viewModel.popBackNav.postValue(true);
            }else {
                showSettingsDialog();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constant.REQUEST_OPEN_SETTING){
            viewModel.popBackNav.postValue(true);
        }
    }

    public void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.mess_dialog_setting)
                .setPositiveButton(getString(R.string.to_settings), (dialog, which) -> {
                    // navigate to settings
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts(Constant.PACKAGE, getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, Constant.REQUEST_OPEN_SETTING);
                    viewModel.popBackNav.postValue(true);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // leave?
                    dialog.dismiss();
                    viewModel.popBackNav.postValue(true);
                }).show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        super.onSupportNavigateUp();

        return Navigation.findNavController(this, R.id.main_nav).navigateUp();
    }
}