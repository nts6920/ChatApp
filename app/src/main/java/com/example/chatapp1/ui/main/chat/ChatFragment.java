package com.example.chatapp1.ui.main.chat;


import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.local.SharedPreferenceHelper;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.databinding.FragmentChatBinding;
import com.example.chatapp1.ui.adapter.ChatMessageAdapter;
import com.example.chatapp1.ui.base.BaseBindingFragment;
import com.example.chatapp1.ui.main.MainActivity;
import com.example.chatapp1.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import timber.log.Timber;


public class ChatFragment extends BaseBindingFragment<FragmentChatBinding, ChatViewModel> {
    private UserMess userMess;
    private Message message;
    private Contact contact;
    private String phone = "" ;
    private ChatMessageAdapter chatMessageAdapter;

    @Override
    protected Class<ChatViewModel> getViewModel() {
        return ChatViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        Timber.e("onCreatedView");

        setAdapter();
        back();
        setBackground();

        if(getArguments() != null) {
            userMess = (UserMess) getArguments().getSerializable(Constant.USER_TO_CHAT);
            if(userMess.getContact() != null){
                phone = userMess.getContact().getPhone();
            }else {
                phone = userMess.getMessage().getAddress();
            }

            getInfoUser();
            sendMess();

            getArguments().clear();
        }

    }

    @Override
    protected void onPermissionGranted() {

    }

    private void getInfoUser() {
        message = userMess.getMessage();
        contact = userMess.getContact();

        if (contact != null) {
            if(contact.getName() == null){
                binding.tvNameUser.setText(contact.getPhone());
            }
            else{
                binding.tvNameUser.setText(contact.getName());
            }

            if(contact.getImage() != null){
                Glide.with(requireContext()).load(Uri.parse(contact.getImage())).into(binding.imUser);

            }
            else {
                binding.imUser.setImageResource(R.drawable.ic_user_contact);
            }
        }
        else{
            binding.tvNameUser.setText(message.getAddress());
        }

        if(message != null){
            message.setReadState(Constant.STATE_READ_MESS);
            userMess.setMessage(message);
        }

        viewModel.updateUserMessToDB(userMess);
    }

    private void observeMess() {
        viewModel.getUserMessByPhone(phone).observe(getViewLifecycleOwner(), messages -> {
            if (chatMessageAdapter != null) {
                binding.rvChatMessage.scrollToPosition(messages.size() - 1);
                Timber.e("LIST MESS "+ messages.size());
                chatMessageAdapter.setMessageList(messages);
            }
        });

    }

    @Override
    public void onResume() {
        Timber.e("onResume");
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        super.onResume();
       observeMess();
    }

    private void sendMess() {
        binding.ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = binding.etEnterMessage.getText().toString();
                if (!msg.equals("")) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    Message mess = new Message(phone, msg, Constant.STATE_READ_MESS, time, Constant.FOLDER_SEND_MESS);

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, msg, null, null);

                    int isSent = smsManager.STATUS_ON_ICC_SENT;
                    if(isSent != Constant.NO_EXIST){
                        Toast.makeText(requireContext(), R.string.sent_mess_successfully, Toast.LENGTH_SHORT).show();

                        viewModel.saveMessToDB(mess);

                        //check phone in usermess
                        int i = viewModel.checkUserMessExistByPhoneInDB(phone);
                        if(i == Constant.EXIST){
                            //have phone in usermess
                            viewModel.getUserByPhone(phone);
                            viewModel.liveUserMess.observe(getViewLifecycleOwner(), userMess -> {
                                if (userMess instanceof UserMess){
                                    Timber.e("HAVE PHONE IN USERMESS");
                                    ((UserMess) userMess).setMessage(mess);
                                    viewModel.updateUserMessToDB(((UserMess) userMess));
                                }
                            });
                        }
                        else{
                            //save usermess
                            UserMess user = new UserMess(mess, contact);
                            viewModel.saveUserMess(user);
                        }
                    }
                    else{
                        Toast.makeText(requireContext(), R.string.sent_mess_error, Toast.LENGTH_SHORT).show();
                    }

                    binding.etEnterMessage.setText("");

                }
            }
        });
    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setReverseLayout(true);
        binding.rvChatMessage.setLayoutManager(linearLayoutManager);
        chatMessageAdapter = new ChatMessageAdapter(getContext());
        binding.rvChatMessage.setAdapter(chatMessageAdapter);
    }

    private void setBackground() {
        mainViewModel.backgroundChat.observe(getViewLifecycleOwner(), s -> {
            mainViewModel.isBackgroundColor.observe(getViewLifecycleOwner(), aBoolean -> {
                if(aBoolean){
                    binding.rvChatMessage.setBackgroundColor(Color.parseColor(s));
                }else {
                    AssetManager assetManager = getResources().getAssets();
                    InputStream is = null;
                    try {
                        is = assetManager.open(Constant.FOLDER_IMG + s);
                        //File exists so do something with it
                        Drawable d = Drawable.createFromStream(is, null);
                        binding.rvChatMessage.setBackground(d);
                    } catch (IOException ex) {
                        //file does not exist
                    } finally {
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void back() {
        binding.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.closeKeyboard(requireContext(), getView());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(userMess.getMessage() == null){
                            viewModel.deleteUserMessInDB(userMess);
                        }
                    }
                }, 300);

                if (requireActivity() instanceof MainActivity) {
                    requireActivity().onBackPressed();
                }

            }
        });
    }

}