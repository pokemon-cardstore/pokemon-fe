package com.example.pokemonshop.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pokemonshop.R;
import com.example.pokemonshop.api.auth.AuthRepository;
import com.example.pokemonshop.api.auth.AuthService;
import com.example.pokemonshop.model.Customer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtU; // ô nhập email
    private EditText edtP; // ô nhập mật khẩu
    private EditText edtCP; // ô nhập xác nhận mật khẩu
    private TextView tvAA; // TextView để chuyển đến màn hình đăng nhập
    private Button btnSU; // nút đăng ký
    private final String REQUIRE = "Không để trống"; // Chuỗi thông báo cho các ô nhập liệu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); //hiển thị toàn màn hình, bao gồm cả các vùng gần cạnh màn hình
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtU = findViewById(R.id.editEmail);
        edtP = findViewById(R.id.editPassword);
        edtCP = findViewById(R.id.editConfirmP);
        tvAA = findViewById(R.id.tvAA);
        btnSU = findViewById(R.id.btnSignUp);

        tvAA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btnSU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtU.getText().toString();
                String password = edtP.getText().toString();
                signup(email, password);
            }
        });

    }

    private void signup(String email, String password) {
        if (!checkInput()) {
            return;
        }
        AuthService authService = AuthRepository.getAuthService();
        Customer customer = new Customer(email, password);
        Call<Void> call = authService.register(customer);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Đăng kí thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(edtU.getText().toString())) {
            edtU.setError(REQUIRE);
            return false;
        }
        if (TextUtils.isEmpty(edtP.getText().toString())) {
            edtP.setError(REQUIRE);
            return false;
        }
        if (TextUtils.isEmpty(edtCP.getText().toString())) {
            edtCP.setError(REQUIRE);
            return false;
        }
        if (!TextUtils.equals(edtP.getText().toString(), edtCP.getText().toString())) {
            Toast.makeText(this, "Mật khẩu không trùng nhau", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
