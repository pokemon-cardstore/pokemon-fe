package com.example.pokemonshop.activity.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonshop.R;
import com.example.pokemonshop.api.Message.MessageRepository;
import com.example.pokemonshop.model.ChatHistoryResponse;
import com.example.pokemonshop.model.MessageDtoRequest;
import com.example.pokemonshop.model.MessageDtoResponse;
import com.example.pokemonshop.adapters.CustomerChatAdapter;
import com.example.pokemonshop.model.Customer;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminChatActivity extends AppCompatActivity {

    private RecyclerView messagesRecyclerView;
    private EditText editTextMessage;
    private Button buttonSend;
    private int customerId;
    private CustomerChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat);
        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAdminChat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Handle back button press
            }
        });
        messagesRecyclerView = findViewById(R.id.messagesRecyclerViews);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        customerId = getIntent().getIntExtra("CustomerId", -1);

        chatAdapter = new CustomerChatAdapter();
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(chatAdapter);

        syncChat();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                editTextMessage.setText("");
            }
        });
    }

    private void syncChat() {
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
        request.setType("ADMIN");
        request.setSendTime(LocalDateTime.now().toString()); // Set the send time here

        chatRef.child("messages").child(messageId).setValue(request)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ChatFragment", "Gửi tin nhắn thành công");
                })
                .addOnFailureListener(e -> Log.e("ChatFragment", "Gửi tin nhắn thất bại", e));
    }
}

