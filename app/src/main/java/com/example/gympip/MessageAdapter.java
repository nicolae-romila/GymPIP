package com.example.gympip;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;


    // ViewHolder class where we set up the attributes
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        // 1. Declare attributes (UI elements)
        TextView messageText;
        TextView senderName;
        TextView timestamp;

        // 2. Constructor - binds XML elements to Java variables
        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            senderName = itemView.findViewById(R.id.message_sender);
            timestamp = itemView.findViewById(R.id.message_time);
        }
    }

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 3. Inflate the item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mess_layout, parent, false);
        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        // 4. Set values for each attribute
        Message message = messages.get(position);
        holder.messageText.setText(message.getText());
        holder.senderName.setText(message.getSenderId());
        Format f=new Format();

        // Format timestamp if needed
        if (!message.getTimestamp().isEmpty()) {
            holder.timestamp.setText(f.formatTime(message.getTimestamp()));
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }
}
