package com.example.chatapp1.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp1.App;
import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.local.SharedPreferenceHelper;
import com.example.chatapp1.databinding.ItemWallpaperBinding;
import com.example.chatapp1.interfaces.SelectWallpaperListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallViewHolder> {
    private List<String> list;
    private final SelectWallpaperListener listener;
   // private int index =  SharedPreferenceHelper.getInt(Constant.ITEM_CLICK_WALLPAPER);
    private int id = -1;

    public WallpaperAdapter(SelectWallpaperListener listener) {
        this.listener = listener;
    }

    public void setListWallpaper(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setIndexClick(String string){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).equals(string)){
                id = i;
                return;
            }
        }
        id = -1;
    }

    public int getId() {
        return id;
    }

    @NonNull
    @Override
    public WallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWallpaperBinding itemWallpaperBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_wallpaper, parent, false);
        return new WallViewHolder(itemWallpaperBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WallViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String file = list.get(position);
        int idWallpaper = list.indexOf(file);

        try {
            InputStream ims = App.getInstance().getAssets().open(Constant.FOLDER_IMG + file);
            Drawable d = Drawable.createFromStream(ims, null);
            Glide.with(App.getInstance()).load(d).into(holder.itemWallpaperBinding.ivItemWallpaper);

        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemWallpaperClick(file);
                if(id != idWallpaper){
                    notifyItemChanged(id);
                    id = idWallpaper;
                    notifyItemChanged(position);
                }
            }
        });

        if(id == idWallpaper){
            holder.itemWallpaperBinding.ivChooseItemWallpaper.setVisibility(View.VISIBLE);
        }
        else{
            holder.itemWallpaperBinding.ivChooseItemWallpaper.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    public class WallViewHolder extends RecyclerView.ViewHolder {
        private final ItemWallpaperBinding itemWallpaperBinding;

        public WallViewHolder(ItemWallpaperBinding itemWallpaperBinding) {
            super(itemWallpaperBinding.getRoot());
            this.itemWallpaperBinding = itemWallpaperBinding;
        }
    }
}
