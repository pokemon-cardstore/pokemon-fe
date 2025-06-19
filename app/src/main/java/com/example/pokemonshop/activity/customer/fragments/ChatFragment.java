package com.example.pokemonshop.activity.customer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pokemonshop.R;
import com.example.pokemonshop.adapters.CustomerChatAdapter;
import com.example.pokemonshop.api.Message.MessageRepository;
import com.example.pokemonshop.model.ChatHistoryResponse;
import com.example.pokemonshop.model.MessageDtoRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private Button buttonSend;
    private CustomerChatAdapter chatAdapter;
    private int customerId;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new CustomerChatAdapter();
        recyclerView.setAdapter(chatAdapter);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        customerId = sharedPreferences.getInt("customerId", -1);

        Log.d("ChatFragment", "Id khách hàng được truy xuất: " + customerId);

        if (customerId != -1) {
            syncChat(customerId);
        } else {
            Toast.makeText(getContext(), "Customer ID không tìm thấy", Toast.LENGTH_SHORT).show();
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                editTextMessage.setText("");
            }
        });

        return view;
    }

    private void syncChat(int customerId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference chatRef = database.getReference("chats").child(String.valueOf(customerId)).child("messages");

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final List<MessageDtoRequest> messages = new ArrayList<>();
                for (DataSnapshot ss : snapshot.getChildren()) {
                    MessageDtoRequest message = ss.getValue(MessageDtoRequest.class);
                    Log.e("ChatFragment", "Message: " + message);
                    // Add each message to your RecyclerView adapter or UI
                    messages.add(message);
                }
                chatAdapter.setMessages(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                MessageDtoRequest newMessage = snapshot.getValue(MessageDtoRequest.class);
                Log.e("ChatFragment", "Message: " + newMessage);
                chatAdapter.addMessage(newMessage);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("ChatFragment", "Failed to load chat history", error.toException());
            }
        });
    }

    private void sendMessage() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference chatRef = database.getReference("chats").child(String.valueOf(customerId));

        String content = editTextMessage.getText().toString();
        if (content.isEmpty()) {
            return;
        }

        String messageId = chatRef.push().getKey();

        Log.d("ChatFragment", "Đang gửi tin nhắn với CustomerId: " + customerId);

        MessageDtoRequest request = new MessageDtoRequest();
        request.setCustomerId(customerId);
        request.setContent(content);
        request.setSendTime(java.time.LocalDateTime.now().toString());
        request.setType("CUSTOMER");

        chatRef.child("messages").child(messageId).setValue(request)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ChatFragment", "Gửi tin nhắn thành công");
                })
                .addOnFailureListener(e -> Log.e("ChatFragment", "Gửi tin nhắn thất bại", e));
    }
}