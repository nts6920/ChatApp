package com.example.chatapp1.ui.main.newmess;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.chatapp1.App;
import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.databinding.FragmentNewMessBinding;
import com.example.chatapp1.interfaces.SelectUserMessListener;
import com.example.chatapp1.ui.adapter.NewMessAdapter;
import com.example.chatapp1.ui.base.BaseBindingFragment;
import com.example.chatapp1.ui.main.MainActivity;
import com.example.chatapp1.utils.Utils;

import java.util.Calendar;

import timber.log.Timber;


public class NewMessFragment extends BaseBindingFragment<FragmentNewMessBinding, NewMessViewModel> implements SelectUserMessListener {
    private NewMessAdapter adapter;

    @Override
    protected Class<NewMessViewModel> getViewModel() {
        return NewMessViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_new_mess;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {
        Timber.e("onCreatedView");
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setupAdapter();

        //back
        binding.tvCancelNewMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvCancelNewMess.setClickable(false);

                requireActivity().getWindow().getDecorView().post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.closeKeyboard(requireContext(), getView());
                    }
                });

                if (requireActivity() instanceof MainActivity) {
                    requireActivity().onBackPressed();
                }
            }
        });

        sendMess();

        searchUser();
        binding.etSendToNew.setBackgroundColor(Color.TRANSPARENT);

    }

    private void searchUser() {
        binding.etSendToNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                if(!s.equals("")){
                    performSearchUserMess(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    private void sendMess() {
        binding.ivSendNewMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.etSendToNew.getText().toString();
                phone = phone.replace(App.getInstance().getString(R.string.to_newmess), "");
                phone = phone.replaceAll("\\s+","");

                String msg = binding.etEnterNewMess.getText().toString();
                if(TextUtils.isDigitsOnly(phone) && !msg.equals("") && !phone.equals("")){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, msg, null, null);

                    long time = Calendar.getInstance().getTimeInMillis();
                    Message message = new Message(phone, msg, Constant.STATE_READ_MESS, time, Constant.FOLDER_SEND_MESS);

                    int isSent = smsManager.STATUS_ON_ICC_SENT;
                    if(isSent != Constant.SENT){
                        Toast.makeText(requireContext(), R.string.sent_mess_successfully, Toast.LENGTH_SHORT).show();
                        viewModel.saveMessToDB(message);
                    }
                    else{
                        Toast.makeText(requireContext(), R.string.sent_mess_error, Toast.LENGTH_SHORT).show();
                    }


                    //check phone in UserMess
                    int userMessExist = viewModel.checkMessUserExistByPhoneInDB(phone);
                    if(userMessExist == Constant.EXIST){
                        //have phone in UserMess
                        viewModel.getUserByPhone(phone);
                        viewModel.userMessLiveData.observe(getViewLifecycleOwner(), userMess -> {
                            userMess.setMessage(message);
                            viewModel.updateUserMessToDB(userMess);

                            actionNewMessToChat(userMess);
                        });
                    }
                    else{
                        int contactExist = viewModel.checkContactExistByPhone(phone);
                        if(contactExist == Constant.EXIST){
                            Timber.e("HAVE PHONE IN CONTACT");

                            viewModel.getContactByPhone(phone);
                            viewModel.contactMutableLiveData.observe(getViewLifecycleOwner(), contact -> {
                                UserMess userMess = new UserMess(message,contact);
                                viewModel.saveUserMess(userMess);

                                viewModel.getUserByPhone(userMess.getContact().getPhone());
                                viewModel.userMessLiveData.observe(getViewLifecycleOwner(), userMess1 -> {
                                    actionNewMessToChat(userMess1);
                                });

                            });
                        }
                        else {
                            Timber.e("DON'T HAVE PHONE");
                            UserMess userMess = new UserMess(message);
                            viewModel.saveUserMess(userMess);

                            viewModel.getUserByPhone(message.getAddress());
                            viewModel.userMessLiveData.observe(getViewLifecycleOwner(), userMess1 -> {
                                actionNewMessToChat(userMess1);
                            });
                        }
                    }

                    binding.etEnterNewMess.getText().clearSpans();
                    binding.etEnterNewMess.getText().clear();
                    Utils.closeKeyboard(requireContext(), getView());
                }
                else{
                    if(msg.equals("")){
                        Toast.makeText(requireContext(), App.getInstance().getString(R.string.have_not_entered_phone_yet), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(requireContext(), App.getInstance().getString(R.string.provide_valid_number), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void setupAdapter() {
        adapter = new NewMessAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvNewMess.setHasFixedSize(true);
        binding.rvNewMess.setLayoutManager(layoutManager);
        binding.rvNewMess.setAdapter(adapter);
    }

    private void performSearchUserMess(String s){
        viewModel.getUsersToSendMess(s);

        viewModel.userMessListLiveData.observe(getViewLifecycleOwner(), userMesses -> {
            if(adapter != null){
                adapter.setList(userMesses);
            }
        });
    }

    @Override
    protected void onPermissionGranted() {

    }
    @Override
    public void onItemUserMessClick(UserMess userMess) {
        actionNewMessToChat(userMess);
    }

    private void actionNewMessToChat(UserMess userMess){
        int currentFragment = Navigation.findNavController(binding.getRoot()).getCurrentDestination().getId();
        if(currentFragment == R.id.newMessFragment){
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.USER_TO_CHAT, userMess);
            if(isAdded()){
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_newMessFragment_to_chatFragment, bundle);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.e("onResume");

        binding.etSendToNew.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.e("onDestroyView");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.e("onDestroy");
    }
}