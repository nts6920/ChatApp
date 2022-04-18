package com.example.chatapp1.ui.main.messages;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.databinding.FragmentMessageBinding;
import com.example.chatapp1.interfaces.SelectUserMessListener;
import com.example.chatapp1.ui.adapter.UserMessAdapter;
import com.example.chatapp1.ui.base.BaseBindingFragment;

import java.util.Collections;
import java.util.Comparator;

import timber.log.Timber;

public class MessageFragment extends BaseBindingFragment<FragmentMessageBinding, MessageViewModel> implements SelectUserMessListener {
    private UserMessAdapter userMessAdapter;
    private final static String[] smsPermission = {Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS};
    private final static int PERMISSION_REQUEST_SMS = 11;
    private boolean isPermissionGrant = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Class getViewModel() {
        return MessageViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                isPermissionGrant = true;
                setupData();
            } else {
                isPermissionGrant = false;
                // You can directly ask for the permission.
                ActivityCompat.requestPermissions(requireActivity(), smsPermission, PERMISSION_REQUEST_SMS);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGrant = true;
                    setupData();
                }
                else{
                    isPermissionGrant = false;
                }

                return;
        }
    }

    private void setupData() {
        setupAdapter();

        binding.btnNewMessage.setVisibility(View.VISIBLE);
        binding.btnNewMessage.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).navigate(R.id.action_homeFragment_to_newMessFragment));

        binding.searchMess.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText!= null && !newText.equals("")){
                    performSearch(newText);
                }
                if(newText.equals("")){
                    observeData();
                }
                return false;
            }
        });
    }

    private void observeData() {
        mainViewModel.getMessUser().observe(getViewLifecycleOwner(), userMessList -> {
            for(int i=0; i<userMessList.size(); i++){
                UserMess userMess = userMessList.get(i);
                if(userMess.getMessage() == null){
                    userMessList.remove(i);
                    viewModel.deleteUsermessInDB(userMess);
                }
            }

            if(userMessList != null){
                userMessAdapter.setUserMessList(userMessList);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
     //   Log.d("NGOCANH", "onStart: ");
    }

    @Override
    public void onResume() {
    //    Log.d("NGOCANH", "onResume: ");
        super.onResume();

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        if(isPermissionGrant = true){
            setupData();
            observeData();
        }

    }

    private void performSearch(String s) {
        viewModel.searchMessUser(s).observe(getViewLifecycleOwner(), userMesses -> {
            if (userMessAdapter != null && userMesses != null) {
                userMessAdapter.setUserMessList(userMesses);
            }
        });
    }

    private void setupAdapter() {
        userMessAdapter = new UserMessAdapter(getContext(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvUserMessage.setLayoutManager(layoutManager);
        binding.rvUserMessage.setAdapter(userMessAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected void onPermissionGranted() {
    }

    @Override
    public void onItemUserMessClick(UserMess userMess) {
        Timber.e("Click user mess id = "+userMess.getId());

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.USER_TO_CHAT, userMess);
        Navigation.findNavController(binding.getRoot()).navigate(R.id.chatFragment, bundle);
    }
}




