package com.example.chatapp1.ui.main.detailcontact;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.chatapp1.App;
import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.databinding.FragmentDetailContactBinding;
import com.example.chatapp1.ui.base.BaseBindingFragment;
import com.example.chatapp1.ui.main.MainActivity;

import timber.log.Timber;

public class DetailContactFragment extends BaseBindingFragment<FragmentDetailContactBinding, DetailContactViewModel> {
    private Contact contact;

    private boolean isLoadData=false;
    private boolean isPermissionGrant = false;

    private final static String[] callPermission = {Manifest.permission.CALL_PHONE};
    private final static int PERMISSION_REQUEST_CALL = 13;

    @Override
    protected Class<DetailContactViewModel> getViewModel() {
        return DetailContactViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_detail_contact;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        Timber.e("onCreatedView");
        setupData();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            toPhoneCall();
            isPermissionGrant = true;
        } else {
            // You can directly ask for the permission.
            isPermissionGrant = false;
            Toast.makeText(requireContext(), "NO_PERMISSION_CALL", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(requireActivity(), callPermission, PERMISSION_REQUEST_CALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toPhoneCall();
                    isPermissionGrant = true;
                }
                else{
                    isPermissionGrant = false;
                    Toast.makeText(requireContext(), App.getInstance().getString(R.string.no_permision_call), Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    private void setupData() {
        toEditContact();

        toChatMess();

        back();
    }

    private void observerData() {
        mainViewModel.detailContact.observe(getViewLifecycleOwner(), contact1 -> {
            contact = contact1;
            setupDataContact(contact1);
        });
       // contact = mainViewModel.detailContact.getValue();

    }

    private void toPhoneCall() {
        binding.layoutDetailContactToContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.e("DETAIL_CONTACT_TO_PHONE_CALL ok");
                String phone = contact.getPhone();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(Constant.URI_TEL+ phone));
                requireActivity().startActivity(intent);
            }
        });
    }

    private void back() {
        binding.ivBackToContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requireActivity() instanceof MainActivity) {
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    private void toEditContact() {
        binding.tvEditDetailContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded()) {
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_detailContactFragment2_to_editContactFragment);
                }
            }
        });
    }

    private void toChatMess() {
        binding.layoutDetailContactToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.layoutDetailContactToMessage.setClickable(false);
                //check phone in usermess
                int i = viewModel.isUserMessExistByIDContactInDB(contact.getId_contact());
                if(i==Constant.EXIST) {
                    Timber.e(" usermess in contact");
                    viewModel.getUserMessByIDContact(contact.getId_contact());
                    viewModel.userMessMutableLiveData.observe(getViewLifecycleOwner(), userMess -> {
                        phoneInMess(userMess);
                    });
                }
                else{
                    UserMess user = new UserMess(contact);
                    phoneInMess(user);
                   // viewModel.saveUserMess(user);

                }
            }
        });
    }

    private void phoneInMess(UserMess userMess){
        int currentFragment = Navigation.findNavController(binding.getRoot()).getCurrentDestination().getId();
        if(currentFragment == R.id.detailContactFragment2){
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.USER_TO_CHAT, userMess);
            if(isAdded()){
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_detailContactFragment2_to_chatFragment, bundle);
            }
        }

    }

    @Override
    public void onDestroyView() {
        Timber.e("onDestroyview");
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();

       binding.layoutDetailContactToMessage.setClickable(true);

        observerData();
    }

    private void setupDataContact(Contact contact){
        binding.tvPhoneUserDetailContact.setText(contact.getPhone());

        if(contact.getName() == null){
            binding.tvNameUserDetailContact.setText(contact.getPhone());
        }
        else{
            binding.tvNameUserDetailContact.setText(contact.getName());
        }

        //get image
        if(contact.getImage() != null){
            Glide.with(requireContext()).load(Uri.parse(contact.getImage())).into(binding.ivUserDetailContact);

        }
        else{
            binding.ivUserDetailContact.setImageResource(R.drawable.ic_user_detail);
        }
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    public void onDestroy() {
        Timber.e("onDestroy");
        super.onDestroy();
    }
}