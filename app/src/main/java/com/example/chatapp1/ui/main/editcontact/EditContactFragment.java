package com.example.chatapp1.ui.main.editcontact;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.chatapp1.App;
import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.databinding.FragmentEditContactBinding;
import com.example.chatapp1.ui.base.BaseBindingFragment;
import com.example.chatapp1.ui.main.MainActivity;
import com.example.chatapp1.utils.Utils;

import timber.log.Timber;

public class EditContactFragment extends BaseBindingFragment<FragmentEditContactBinding, EditContactViewModel> {

    private int editImage = 0;
    private Contact contact;
    private Uri selectedImage;
    private int haveImage = 0;
   // private Bitmap bitmap1 = null;

    private final static String[] mediaPermission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final static int PERMISSION_REQUEST_MEDIA = 15;

    @Override
    protected Class<EditContactViewModel> getViewModel() {
        return EditContactViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_edit_contact;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        Timber.e("onCreatedView");

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        binding.etUserName.setSingleLine();
        binding.etUserName.setEllipsize(TextUtils.TruncateAt.START);


        getDetailContact();
//        checkDone();
//        addImage();

        binding.ivDelNameContact.setOnClickListener(v -> binding.etUserName.setText(""));
        binding.ivDelPhoneContact.setOnClickListener(v -> binding.etUserPhone.setText(""));

        backToDetailFrm();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            checkDone();
            addImage();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), mediaPermission, PERMISSION_REQUEST_MEDIA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_MEDIA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkDone();
                    addImage();
                }
                return;
        }

    }

    private Bitmap getBitmapFromURI(String uri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(App.getInstance().getContentResolver() , Uri.parse(uri));
            return bitmap;
        }
        catch (Exception e) {
        }
        return null;
    }

    private void checkDone() {
        binding.tvDoneEdit.setOnClickListener(v -> {
            String name = binding.etUserName.getText().toString();
            String phone = binding.etUserPhone.getText().toString();

            if(editImage == Constant.CLICK){
                String img = selectedImage.toString();
                Bitmap bitmap = getBitmapFromURI(img);

                contact.setImage(img);

                if(haveImage == 1){
                    Timber.e("UPDATE IMAGE TO PHONE");
                    viewModel.updateImageContact(contact.getId_contact(), bitmap);
                }
                else{
                    Timber.e("INSERT IMAGE TO PHONE");
//                     insertImage(contact.getId_contact(), contact.getImage());
                    viewModel.insertImageContact(contact.getId_contact(), bitmap);
                }
            }

            if(name.equals("")){
                contact.setName(null);
            }
            else{
                contact.setName(name);
            }

            contact.setPhone(phone);

            updateContact();

        });

        binding.etUserPhone.setOnTouchListener((v, event) -> {
            binding.tvDoneEdit.setTextColor(getResources().getColor(R.color.primary));
            binding.ivDelPhoneContact.setVisibility(View.VISIBLE);
            binding.ivDelNameContact.setVisibility(View.GONE);

            return false;
        });
        binding.etUserName.setOnTouchListener((v, event) -> {
            binding.ivDelNameContact.setVisibility(View.VISIBLE);
            binding.ivDelPhoneContact.setVisibility(View.GONE);
            binding.tvDoneEdit.setTextColor(getResources().getColor(R.color.primary));
            return false;
        });
    }

    private void updateContact() {
        viewModel.updateNameContact(contact.getId_contact(), contact.getName());
        viewModel.updatePhoneContact(contact.getId_contact(), contact.getPhone());

        Timber.e("UPDATE CONTACT WITH NAME = "+contact.getName()+"--PHONE = "+contact.getPhone());

        viewModel.updateContact(contact);
        mainViewModel.detailContact.setValue(contact);

        //update user mess by id contact
        int i = viewModel.isUserMessExistByIDContactInDB(contact.getId_contact());

        if(i==Constant.EXIST){
            viewModel.getUserMessByIDContact(contact.getId_contact());
            viewModel.userMessEditContactMutableLiveData.observe(getViewLifecycleOwner(), userMess -> {
                userMess.setContact(contact);
                viewModel.updateUserMess(userMess);
                Timber.e(" updated usermess name = "+userMess.getContact().getName()+"--"+userMess.getContact().getPhone());
            });
        }
        else{
            Timber.e(" contact not in usermess");
            UserMess user = new UserMess(contact);
            viewModel.saveUserMess(user);
        }

        binding.etUserPhone.setCursorVisible(false);
        binding.etUserName.setCursorVisible(false);
        Utils.closeKeyboard(requireContext(), getView());

        binding.tvDoneEdit.setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (requireActivity() instanceof MainActivity) {
                    requireActivity().onBackPressed();
                }
            }
        }, 500);

    }

    private void backToDetailFrm() {
        binding.tvCancelEdit.setOnClickListener(v -> {
           binding.tvCancelEdit.setClickable(false);

            if (requireActivity() instanceof MainActivity) {
                requireActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        Timber.e("onResume");

       binding.tvAddImage.setClickable(true);

        super.onResume();
    }

    private void addImage() {
        binding.tvAddImage.setOnClickListener(v -> {
            binding.tvAddImage.setClickable(false);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permission, Constant.PERMISSION_CODE);
                }
                else{
                    pickImageFromGallery();
                }
            }
        });
    }

    private void getDetailContact(){
        mainViewModel.detailContact.observe(getViewLifecycleOwner(), contact1 -> {
            setupDataContact(contact1);
        });
        contact = mainViewModel.detailContact.getValue();
        if(contact.getImage() != null){
            haveImage = 1;
        }
    }

    private void setupDataContact(Contact contact){
        binding.etUserPhone.setText(contact.getPhone());
        binding.etUserName.setText(contact.getName());

        //get image
        if(contact.getImage() != null){
            Glide.with(requireContext()).load(Uri.parse(contact.getImage())).into(binding.ivImageEditContact);

            binding.tvAddImage.setText(getString(R.string.contact));
        }
        else{
            //  Timber.e("DON'T HAVE IMAGE");
            binding.tvAddImage.setText(getString(R.string.add_image));
            binding.ivImageEditContact.setImageResource(R.drawable.ic_user_edit);
        }
    }

    private void pickImageFromGallery() {

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType(Constant.TYPE_IMAGE_VIDEO);
        startActivityForResult(pickIntent, Constant.IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == Constant.IMAGE_PICK_CODE){
                 selectedImage = data.getData();
                if (selectedImage.toString().contains(Constant.TYPE_IMAGE)) {
                    Glide.with(requireContext()).load(selectedImage).into(binding.ivImageEditContact);

                    binding.tvAddImage.setText(getString(R.string.contact));
                    binding.tvAddImage.setTextColor(getResources().getColor(R.color.primary));
                    binding.tvDoneEdit.setTextColor(getResources().getColor(R.color.primary));

                    binding.tvAddImage.setClickable(true);

                    editImage=Constant.CLICK;

                }
                else  if (selectedImage.toString().contains(Constant.TYPE_VIDEO)) {
                    //handle video
                    Toast.makeText(requireContext(), App.getInstance().getString(R.string.no_support_get_video), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onPermissionGranted() {

    }

}