package com.dam.daniel.playmatch.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dam.daniel.playmatch.R;
import com.dam.daniel.playmatch.models.Message;
import com.dam.daniel.playmatch.utils.DateTimeUtils;
import com.dam.daniel.playmatch.utils.LocalSorage;

import java.util.List;

public class ChatBoxAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Message> messageList;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public ChatBoxAdapter(Context context, List<Message> messagesList) {
        this.context = context;
        this.messageList = messagesList;
    }

    public int getItemCount() {
        return messageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        if (message.getUserSender() == LocalSorage.loadDataFromSharedPreferences(context).getUser().getId()) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_my, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_their, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message m = messageList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(m);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(m);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.message);
            timeText = (TextView) itemView.findViewById(R.id.messageDate);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            // Format Date String
            timeText.setText( DateTimeUtils.getTimeChatBoxMessage(message.getDate()) );
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.message);
            timeText = (TextView) itemView.findViewById(R.id.messageDate);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            // Format Date String
            timeText.setText( DateTimeUtils.getTimeChatBoxMessage(message.getDate()) );
        }
    }

}