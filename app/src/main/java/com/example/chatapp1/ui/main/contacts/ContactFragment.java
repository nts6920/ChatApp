package com.example.chatapp1.ui.main.contacts;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.databinding.FragmentContactBinding;
import com.example.chatapp1.interfaces.SelectContactListener;
import com.example.chatapp1.interfaces.SelectListener;
import com.example.chatapp1.ui.adapter.ContactAdapter;
import com.example.chatapp1.ui.base.BaseBindingFragment;
import com.example.chatapp1.ui.main.MainActivity;

import java.util.List;
import java.util.Map;

import timber.log.Timber;


public class ContactFragment extends BaseBindingFragment<FragmentContactBinding, ContactViewModel> implements SelectContactListener {
    private ContactAdapter contactAdapter;
    private boolean isLoadData=false;
    private final static String[] contactPermission = {Manifest.permission.READ_CONTACTS};
    private final static int PERMISSION_REQUEST_CONTACT = 10;

    @Override
    protected Class getViewModel() {
        return ContactViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            setupData();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), contactPermission, PERMISSION_REQUEST_CONTACT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupData();
                }
                return;
        }
    }

    private void setupData(){
        setupAdapter();

        binding.searchContacts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    private void performSearch(String s) {
        viewModel.searchContactList(s);
        viewModel.contactSearch.observe(getViewLifecycleOwner(),contacts -> {
            if(contactAdapter != null && contacts!=null){
                contactAdapter.setContactList(contacts);
            }
        } );
    }

    @Override
    protected void onPermissionGranted() {
    }

    private void setupAdapter() {
        contactAdapter = new ContactAdapter(getContext(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvContacts.setLayoutManager(layoutManager);
        binding.rvContacts.setAdapter(contactAdapter);
    }

    private void observeData(){
        viewModel.getListContact().observe(getViewLifecycleOwner(), contacts -> {
            if(contactAdapter != null){
                contactAdapter.setContactList(contacts);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.e("onResume");
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        observeData();

        mainViewModel.saveListContactFromPhone();

    }


    @Override
    public void onItemClick(Contact contact) {
        mainViewModel.detailContact.setValue(contact);

        if(isAdded()){
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_homeFragment_to_detailContactFragment2);
        }
    }
}