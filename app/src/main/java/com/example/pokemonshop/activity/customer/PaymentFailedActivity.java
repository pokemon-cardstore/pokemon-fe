package com.example.pokemonshop.activity.customer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pokemonshop.R;
import com.example.pokemonshop.model.CartItem;

import java.util.List;

public class PaymentFailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_failed);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Uri data = getIntent().getData();
        if (data != null) {
            String orderId = data.getQueryParameter("orderId");

            TextView tvMessage = findViewById(R.id.tvFailedMessage);
            tvMessage.setText("Thanh toán thất bại!\nMã đơn hàng: " + orderId);
        }

        Button btnBackToHomePage = findViewById(R.id.btnBackToHomePage);
        btnBackToHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentFailedActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}