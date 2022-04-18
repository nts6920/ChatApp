package com.example.chatapp1.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp1.App;
import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.local.SharedPreferenceHelper;
import com.example.chatapp1.data.model.Setting;
import com.example.chatapp1.databinding.ItemSettingBinding;
import com.example.chatapp1.interfaces.SelectSettingListener;

import java.util.List;

import company.librate.SelectRateApp;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.SettingViewHolder>{
    private List<Setting> list;
    private SelectSettingListener settingListener;
    private SelectRateApp selectRateApp;

    private Context context;

    public SettingAdapter(SelectSettingListener settingListener, Context context) {
        this.settingListener = settingListener;
        this.context = context;
    }

    public void setSettingList(List<Setting> list){
        this.list = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSettingBinding settingBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_setting, parent, false);
        return new SettingViewHolder(settingBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {

        Setting setting = list.get(position);

        holder.settingBinding.tvItemSetting.setText(setting.getName());
        holder.settingBinding.ivImageItemSetting.setImageResource(setting.getImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingListener.onItemSettingClick(setting);
            }
        });

    }

    public void removeRate() {
        if(list.size() > 3){
            list.remove(3);
            notifyItemRemoved(3);
            notifyItemRangeChanged(3, list.size());
        }
    }

    @Override
    public int getItemCount() {
        if(list != null){
            return list.size();
        }
        return 0;
    }

    public class SettingViewHolder extends RecyclerView.ViewHolder {
        ItemSettingBinding settingBinding;

        public SettingViewHolder(ItemSettingBinding itemView) {

            super(itemView.getRoot());
            settingBinding = itemView;


        }
    }
}
