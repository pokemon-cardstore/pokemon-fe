package com.example.pokemonshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonshop.R;
import com.example.pokemonshop.model.MessageDtoRequest;

import java.util.ArrayList;
import java.util.List;

public class CustomerChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_CUSTOMER = 1;
    private static final int VIEW_TYPE_ADMIN = 2;

    private List<MessageDtoRequest> messages;

    public void addMessage(MessageDtoRequest message) {
        if (messages == null) {
            messages = new ArrayList<MessageDtoRequest>();
        }

        messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        MessageDtoRequest message = messages.get(position);
        if ("CUSTOMER".equals(message.getType())) {
            return VIEW_TYPE_CUSTOMER;
        } else {
            return VIEW_TYPE_ADMIN;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_CUSTOMER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_customer, parent, false);
            return new CustomerViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_admin, parent, false);
            return new AdminViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageDtoRequest message = messages.get(position);
        if (holder instanceof CustomerViewHolder) {
            ((CustomerViewHolder) holder).bind(message);
        } else {
            ((AdminViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    public void setMessages(List<MessageDtoRequest> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage, textViewTime;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
        }

        void bind(MessageDtoRequest message) {
            textViewMessage.setText(message.getContent());
            textViewTime.setText(message.getSendTime());
        }
    }

    class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage, textViewTime;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
        }

        void bind(MessageDtoRequest message) {
            textViewMessage.setText(message.getContent());
            textViewTime.setText(message.getSendTime());
        }
    }
}
