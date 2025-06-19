package com.example.pokemonshop.activity.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.pokemonshop.activity.admin.AdminDashboardActivity;
import com.example.pokemonshop.activity.customer.MainActivity;
import com.example.pokemonshop.R;
import com.example.pokemonshop.api.auth.AuthRepository;
import com.example.pokemonshop.api.auth.AuthService;
import com.example.pokemonshop.model.LoginResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvNotAccountYet;
    private Button btnSignIn;

    private final String REQUIRE = "Không để trống";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Ánh xạ các thành phần giao diện với các thành phần trong layout
        etUsername = findViewById(R.id.edtU);
        etPassword = findViewById(R.id.edtP);
        tvNotAccountYet = findViewById(R.id.tvAY);
        btnSignIn = findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(v -> {
            String email = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            loginUser(email, password);
        });

        // Đăng kí
        tvNotAccountYet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        if (!checkInput()) {
            return;
        }
        AuthService authService = AuthRepository.getAuthService();
        Call<LoginResponse> call = authService.login(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        String accessToken = loginResponse.getAccessToken();
                        // Save the accessToken using SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("accessToken", accessToken);
                        editor.apply();

                        try {
                            String[] decodedParts = JWTUtils.decoded(accessToken);
                            String body = decodedParts[1];

                            // Parse the body to get the role and possibly the CustomerId
                            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                            String role = jsonObject.get("Role").getAsString();
                            int customerId = -1;
                            Intent intent = null;

                            if (role != null) {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                if (role.equals("ADMIN")) {
                                    intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                                } else if (role.equals("CUSTOMER")) {
                                    intent = new Intent(LoginActivity.this, MainActivity.class);
                                    if (jsonObject.has("CustomerId")) {
                                        customerId = jsonObject.get("CustomerId").getAsInt();
                                        editor.putInt("customerId", customerId);
                                        editor.apply();
                                    }
                                }

                                if (intent != null) {
                                    intent.putExtra("accessToken", accessToken);
                                    startActivity(intent);
                                    finish(); // Close LoginActivity
                               //     checkAndRequestNotificationPermission(customerId);
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Không tìm thấy Role ở token", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Decode token thất bại", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: Không nhận được token", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (response.code() == 404) {
                        Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginActivity", "Lỗi đăng nhập: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(etUsername.getText().toString())) {
            etUsername.setError(REQUIRE);
            return false;
        }
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError(REQUIRE);
            return false;
        }
        return true;
    }


}
