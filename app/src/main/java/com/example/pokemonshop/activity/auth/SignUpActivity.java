package com.example.pokemonshop.activity.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.pokemonshop.model.RegisterDto;
import com.google.gson.Gson;

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
        if (!checkInput()) return;

        AuthService authService = AuthRepository.getAuthService();

        String confirmPassword = edtCP.getText().toString();
        String username = email.split("@")[0];

        RegisterDto dto = new RegisterDto(username, email, password, confirmPassword);

        // 💥 Log JSON gửi đi
        Log.d("DTO_JSON", new Gson().toJson(dto));

        Call<Void> call = authService.register(dto);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new AlertDialog.Builder(SignUpActivity.this)
                            .setTitle("Đăng ký thành công")
                            .setMessage("🎉 Vui lòng kiểm tra email để kích hoạt tài khoản trước khi đăng nhập.")
                            .setPositiveButton("Đăng nhập", (dialog, which) -> {
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .setCancelable(false)
                            .show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Đăng ký thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private boolean checkInput() {
        String email = edtU.getText().toString().trim();
        String password = edtP.getText().toString().trim();
        String confirmPassword = edtCP.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtU.setError("Email không được để trống");
            return false;
        }

        if (!email.endsWith("@gmail.com") && !email.endsWith("@fpt.edu.vn")) {
            edtU.setError("Email phải có đuôi @gmail.com hoặc @fpt.edu.vn");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            edtP.setError("Mật khẩu không được để trống");
            return false;
        }

        if (password.length() < 6) {
            edtP.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            edtCP.setError("Xác nhận mật khẩu không được để trống");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            edtCP.setError("Mật khẩu xác nhận không khớp");
            Toast.makeText(this, "Mật khẩu không trùng nhau", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
