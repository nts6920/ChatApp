package com.example.chatapp1.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp1.R;
import com.example.chatapp1.common.Constant;
import com.example.chatapp1.data.model.Message;
import com.example.chatapp1.databinding.ReceiveBinding;
import com.example.chatapp1.databinding.SendBinding;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Message> messageList;

    public ChatMessageAdapter(Context context) {
        this.context = context;
        this.messageList = messageList;
    }

    public void setMessageList(List<Message> messageList){
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Constant.TYPE_SEND_MESS) {
            SendBinding sendBinding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.send, parent, false);
            return new SenderViewHolder(sendBinding);
        }
        ReceiveBinding receiveBinding =  DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.receive, parent, false);
        return new ReceiverViewHolder(receiveBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder) holder).sendBinding.tvSendMessage.setText(message.getMsg());
        }
        else  if(holder.getClass() == ReceiverViewHolder.class){

            ((ReceiverViewHolder) holder).receiveBinding.tvReceiveMessage.setText(message.getMsg());
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if(message.getFolderName().equals(Constant.FOLDER_RECEIVE_MESS)){
            return Constant.TYPE_RECEIVE_MESS;
        }
        return Constant.TYPE_SEND_MESS;
    }

    @Override
    public int getItemCount() {
        if(messageList != null)
            return messageList.size();
        return 0;
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        SendBinding sendBinding;

        public SenderViewHolder(SendBinding itemView) {
            super(itemView.getRoot());

            sendBinding = itemView;
        }
    }
    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        ReceiveBinding receiveBinding;
        public ReceiverViewHolder(ReceiveBinding itemView) {
            super(itemView.getRoot());

            receiveBinding = itemView;
        }
    }
}
