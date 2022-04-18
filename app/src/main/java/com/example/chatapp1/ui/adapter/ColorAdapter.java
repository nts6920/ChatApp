package com.example.chatapp1.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
import com.example.chatapp1.databinding.ItemColorBinding;
import com.example.chatapp1.interfaces.SelectStringListener;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {
    private List<String> list;
    private SelectStringListener listener;
  //  private int index = SharedPreferenceHelper.getInt(Constant.ITEM_CLICK_COLOR);
    private int id = -1;


    public ColorAdapter(SelectStringListener listener){
        this.listener = listener;
    }
    public void setListColor(List<String> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public int getId() {
        return id;
    }

    public void setIndexClick(String pickedColor){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).equals(pickedColor)){
                id = i;
                return;
            }
        }
        id = -1;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemColorBinding itemColorBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_color, parent, false);
        return new ColorViewHolder(itemColorBinding);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String color = list.get(position);
        int idColor = list.indexOf(color);

        holder.itemColorBinding.ivItemColor.setBackgroundColor(Color.parseColor(color));

        if(id == 0){
            setPickColor(holder);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemStringClick(color);
                if(id != idColor){
                    notifyItemChanged(id);
                    id = idColor;
                    notifyItemChanged(position);
                }
            }
        });

        if(id == idColor){
            setPickColor(holder);
        }
        else{
            holder.itemColorBinding.cardviewColor.setBackground(null);
            holder.itemColorBinding.cardviewColor2.setBackground(App.getInstance().getDrawable(R.drawable.bg_circle2));
            holder.itemColorBinding.cardviewColor.setScaleX(1f);
            holder.itemColorBinding.cardviewColor.setScaleY(1f);
        }
    }

    private void setPickColor(ColorViewHolder holder) {
        holder.itemColorBinding.cardviewColor.setBackground(App.getInstance().getDrawable(R.drawable.bg_color));
        holder.itemColorBinding.cardviewColor2.setBackground(App.getInstance().getDrawable(R.drawable.bg_color2));
        holder.itemColorBinding.cardviewColor.setScaleX(0.8f);
        holder.itemColorBinding.cardviewColor.setScaleY(0.8f);
    }

    @Override
    public int getItemCount() {
        if(list != null){
            return list.size();
        }
        return 0;
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {
        ItemColorBinding itemColorBinding;
        public ColorViewHolder(ItemColorBinding itemView) {
            super(itemView.getRoot());

            itemColorBinding = itemView;
        }
    }
}
