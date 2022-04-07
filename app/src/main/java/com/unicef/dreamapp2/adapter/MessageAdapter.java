package com.unicef.dreamapp2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.unicef.dreamapp2.R;
import com.unicef.dreamapp2.model.MessageModel;

import java.util.List;

/**
 * @author Iman Augustine
 *
 * MessageAdapter is used as messages adapter for the RecyclerView
 * used to display the chat messages between two users.
 *
 * */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<MessageModel> messageList;
    private String userId;
    private String chatterName;
    private Context context;

    public MessageAdapter(List<MessageModel> messageList, String userId, String chatterName, Context context) {
        this.messageList = messageList; // Message list
        this.userId = userId; // Current user's id
        this.chatterName = chatterName; // Other sender's name
        this.context = context; // Context
    }

    public void setValues(List<MessageModel> messageList) {
         this.messageList.clear();
         if(messageList!=null) {
             this.messageList.addAll(messageList);
         }
         this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);

        return new MessageViewHolder(itemView);
    }

    // On bind view holder
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        // Message model item
        MessageModel message = messageList.get(position);

        // The current user's own message
        if ( message.senderId.equals(userId) ) {
            holder.messageText1.setVisibility(View.GONE); // Showing message
            holder.messageText2.setVisibility(View.VISIBLE); // Showing message
            holder.messageText2.setText((context.getString(R.string.you_sender_text)+"\n" + message.message)); // Setting message text
        } else { // The other chatter's message
            holder.messageText2.setVisibility(View.GONE); // Showing message
            holder.messageText1.setVisibility(View.VISIBLE); // Showing message
            holder.messageText1.setText((chatterName + ":\n" + message.message)); // Setting message text
        }
    }

    @Override
    public int getItemCount() {
        return messageList != null ? messageList.size() : 0;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        // Global variables
        private TextView messageText1;
        private TextView messageText2;

        // Message view holder
        private MessageViewHolder(View view) {
            super(view);
            // Views initialize
            messageText1 = view.findViewById(R.id.messageText1);
            messageText2 = view.findViewById(R.id.messageText2);
        }
    }
}
