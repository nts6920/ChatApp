package com.example.chatapp1.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
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
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.databinding.ItemMessBinding;
import com.example.chatapp1.interfaces.SelectUserMessListener;
import com.example.chatapp1.utils.Utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class UserMessAdapter extends RecyclerView.Adapter<UserMessAdapter.UserMessViewHolder> {
    private List<UserMess> userMessList;
    private Context context;
    private SelectUserMessListener listener;

    public UserMessAdapter(Context context, SelectUserMessListener listener) {
        this.context = context;
        this.listener = listener;
    }
    public void setUserMessList(List<UserMess> list){
        userMessList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserMessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMessBinding itemMessBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_mess, parent, false);
        return new UserMessViewHolder(itemMessBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserMessViewHolder holder, int position) {
        UserMess userMess = userMessList.get(position);

        Message message = userMess.getMessage();
        Contact contact = userMess.getContact();

        if(message == null){
            Timber.e("IGNORE_USERMESS");
        }
        else{
            String time="";
            try {
                time = Utils.transDateString(message.getTime());
            } catch (NumberFormatException e) {
                return;
            }

            String date = time.substring(0,10);
            String hour = time.substring(11, 19);
            int i=0;

            SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_TIME);
            try {
                Date strDate = sdf.parse(date);
                Date currentTime = sdf.parse(sdf.format(new Date()));
                if (currentTime.after(strDate)) {
                    i = 1;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(i==1){
                holder.itemMessBinding.tvTimeItemMessage.setText(date);
            }
            else{
                holder.itemMessBinding.tvTimeItemMessage.setText(hour);
            }

            if(contact != null){
                if(contact.getName() == null){
                    holder.itemMessBinding.tvUserItemMessage.setText(contact.getPhone());
                }
                else{
                    holder.itemMessBinding.tvUserItemMessage.setText(contact.getName());
                }

                if(contact.getImage() != null){
                    Glide.with(context).load(Uri.parse(contact.getImage())).into(holder.itemMessBinding.ivUserItemMessage);
                }
                else{
                    holder.itemMessBinding.ivUserItemMessage.setImageResource(R.drawable.ic_user_detail);
                }
            }
            else{
                holder.itemMessBinding.tvUserItemMessage.setText(message.getAddress());
                holder.itemMessBinding.ivUserItemMessage.setImageResource(R.drawable.ic_user_detail);
            }

            holder.itemMessBinding.tvBodyItemMessage.setText(message.getMsg());

            String state = message.getReadState();
            if(state.equals(Constant.STATE_NOT_READ_MESS)){
                holder.itemMessBinding.tvBodyItemMessage.setTypeface(Typeface.DEFAULT_BOLD);
                holder.itemMessBinding.tvBodyItemMessage.setTextColor(Color.BLACK);
            }
            else{
                holder.itemMessBinding.tvBodyItemMessage.setTypeface(Typeface.DEFAULT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.itemMessBinding.tvBodyItemMessage.setTextColor(App.getInstance().getColor(R.color.grey_text));
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemUserMessClick(userMess);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(userMessList != null)
            return userMessList.size();
        return 0;
    }


    public static class UserMessViewHolder extends RecyclerView.ViewHolder {
        ItemMessBinding itemMessBinding;

        public UserMessViewHolder(ItemMessBinding itemView) {
            super(itemView.getRoot());
            itemMessBinding = itemView;
        }
    }
}
