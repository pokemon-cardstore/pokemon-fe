package com.example.pokemonshop.activity.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.example.pokemonshop.R;
import com.example.pokemonshop.activity.auth.LoginActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    // Các biến CardView đại diện cho các thẻ trên Dashboard
    private CardView category, product, chatBox, order, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Kích hoạt chế độ EdgeToEdge cho trải nghiệm toàn màn hình
        EdgeToEdge.enable(this);
        setContentView(com.example.pokemonshop.R.layout.admin_dashboard);

        // Đặt padding cho view dựa vào Insets của hệ thống (ví dụ: thanh trạng thái)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        category = findViewById(R.id.categoryCard);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        product = findViewById(R.id.productCard);
        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });


        logout = findViewById(R.id.logoutCard);
        logout.setOnClickListener(v -> signOut());

        chatBox = findViewById(R.id.chatboxCard);
        chatBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ChatCustomerListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signOut() {
        String accessToken = getIntent().getStringExtra("accessToken");
        if(accessToken == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("accessToken");
            editor.apply();
        }

        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
