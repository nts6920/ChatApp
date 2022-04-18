package com.example.chatapp1.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.local.SharedPreferenceHelper;
import com.example.chatapp1.data.model.Font;
import com.example.chatapp1.databinding.ItemFontBinding;
import com.example.chatapp1.interfaces.SelectFontListener;

import java.util.List;

public class EditFontAdapter extends RecyclerView.Adapter<EditFontAdapter.EditFontViewHolder> {

    private final Context context;
    private List<Font> listFont;
    private SelectFontListener listener;
    private int index =  SharedPreferenceHelper.getInt(Constant.ITEM_EDIT_FONT);

    public EditFontAdapter(Context context, SelectFontListener listener){
        this.context = context;
        this.listener = listener;
    }

    public void setListFont(List<Font> listFont){
        this.listFont = listFont;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EditFontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFontBinding itemFontBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_font, parent, false);
        return new EditFontViewHolder(itemFontBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EditFontViewHolder holder, int position) {
        int i = holder.getAdapterPosition();
        Font font = listFont.get(i);
//        int style = font.getFont();

        holder.itemFontBinding.tvNameItemFont.setText(font.getName());

        Typeface fontAssets = Typeface.createFromAsset(context.getAssets(), Constant.FOLDER_FONT+font.getFont());

        holder.itemFontBinding.tvNameItemFont.setTypeface(fontAssets);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = i;
                SharedPreferenceHelper.storeInt(Constant.ITEM_EDIT_FONT, index);
                notifyDataSetChanged();
                listener.onItemClick(font.getFont());
            }
        });
        if (index == i) {
            holder.itemFontBinding.ivItemFont.setVisibility(View.VISIBLE);
        }
        else {
            holder.itemFontBinding.ivItemFont.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        if(listFont != null){
            return listFont.size();
        }
        return 0;
    }

    public class EditFontViewHolder extends RecyclerView.ViewHolder{
        ItemFontBinding itemFontBinding;
        public EditFontViewHolder(ItemFontBinding itemView) {
            super(itemView.getRoot());

            itemFontBinding = itemView;
        }
    }
}
