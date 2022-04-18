package com.example.chatapp1.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp1.R;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.data.model.UserMess;
import com.example.chatapp1.databinding.ItemNewMessBinding;
import com.example.chatapp1.interfaces.SelectUserMessListener;

import java.util.List;

public class NewMessAdapter extends RecyclerView.Adapter<NewMessAdapter.NewMessViewHolder>{
    private List<UserMess> list;
    private SelectUserMessListener listener;

    public NewMessAdapter(SelectUserMessListener listener) {
        this.listener = listener;
    }

    public void setList(List<UserMess> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewMessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNewMessBinding itemNewMessBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_new_mess, parent, false);
        return new NewMessViewHolder(itemNewMessBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewMessViewHolder holder, int position) {
        UserMess userMess = list.get(position);
        Contact contact = userMess.getContact();
        Message message = userMess.getMessage();

        if(contact != null){
            holder.itemNewMessBinding.tvNameItemUserMess.setText(contact.getName());
            holder.itemNewMessBinding.tvPhoneItemUserMess.setText(contact.getPhone());
        }
        else {
            holder.itemNewMessBinding.tvNameItemUserMess.setText(message.getAddress());
            holder.itemNewMessBinding.tvPhoneItemUserMess.setText(message.getAddress());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemUserMessClick(userMess);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list != null){
            return list.size();
        }
        return 0;
    }

    public class NewMessViewHolder extends RecyclerView.ViewHolder {
        ItemNewMessBinding itemNewMessBinding;

        public NewMessViewHolder(ItemNewMessBinding itemView) {
            super(itemView.getRoot());
            itemNewMessBinding = itemView;
        }
    }
}
