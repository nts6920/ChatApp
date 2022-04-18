package com.example.chatapp1.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp1.App;
import com.example.chatapp1.R;
import com.example.chatapp1.data.model.Contact;
import com.example.chatapp1.databinding.ItemContactBinding;
import com.example.chatapp1.interfaces.SelectContactListener;

import java.io.File;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    List<Contact> contactList;
    private Context context;
    private SelectContactListener listener;

    public ContactAdapter(Context context, SelectContactListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContactBinding itemContactBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_contact, parent, false);
        return new ContactViewHolder(itemContactBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        if(contact.getName() == null){
            holder.itemContactBinding.tvItemContact.setText(contact.getPhone());
        }
        else{
            holder.itemContactBinding.tvItemContact.setText(contact.getName());
        }

        if(contact.getImage() != null) {
            Glide.with(context).load(Uri.parse(contact.getImage())).into(holder.itemContactBinding.ivImageItemContact);
        }
        else{
            holder.itemContactBinding.ivImageItemContact.setImageResource(R.drawable.ic_user_detail);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(contactList != null)
            return contactList.size();
        return 0;
    }
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        ItemContactBinding itemContactBinding;

        public ContactViewHolder(ItemContactBinding itemView) {
            super(itemView.getRoot());

            itemContactBinding = itemView;
        }
    }
}
