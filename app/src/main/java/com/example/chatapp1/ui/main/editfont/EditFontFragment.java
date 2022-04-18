package com.example.chatapp1.ui.main.editfont;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;

import com.example.chatapp1.App;
import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.local.SharedPreferenceHelper;
import com.example.chatapp1.databinding.FragmentEditFontBinding;
import com.example.chatapp1.interfaces.SelectFontListener;
import com.example.chatapp1.ui.adapter.EditFontAdapter;
import com.example.chatapp1.ui.base.BaseBindingFragment;
import com.example.chatapp1.ui.main.MainActivity;


public class EditFontFragment extends BaseBindingFragment<FragmentEditFontBinding, EditFontViewModel> implements SelectFontListener {
    private EditFontAdapter adapter;

    @Override
    protected Class<EditFontViewModel> getViewModel() {
        return EditFontViewModel.class;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_edit_font;
    }

    @Override
    protected void onCreatedView(View view, Bundle savedInstanceState) {

        if(SharedPreferenceHelper.getBoolean(Constant.LOAD_FONT_FIRST_TIME, true)) {
            SharedPreferenceHelper.storeInt(Constant.ITEM_EDIT_FONT, 0);
            SharedPreferenceHelper.storeBoolean(Constant.LOAD_FONT_FIRST_TIME, false);
        }

        setUpAdapter();
        setFontData();

        binding.ivBackToSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requireActivity() instanceof MainActivity) {
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    private void setUpAdapter() {
        adapter = new EditFontAdapter(getContext(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvFont.setLayoutManager(linearLayoutManager);
        binding.rvFont.setAdapter(adapter);
    }

    private void setFontData(){
       // viewModel.getListFont(Constant.FOLDER_FONT, requireContext());

        viewModel.getListFont();
        viewModel.fontList.observe(this, strings -> {
            if (strings != null){
               adapter.setListFont(strings);
            }
        });
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    public void onItemClick(String font) {
        SharedPreferenceHelper.storeString(Constant.PREF_FONT_CURRENT, font);
        App.getInstance().setFont(font);

        //restart activity
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        requireActivity().startActivity(intent);
        ActivityCompat.finishAffinity(requireActivity());
    }
}