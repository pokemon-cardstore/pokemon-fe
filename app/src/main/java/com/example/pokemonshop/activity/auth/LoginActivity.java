package com.example.pokemonshop.activity.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokemonshop.R;
import com.example.pokemonshop.activity.admin.AdminDashboardActivity;
import com.example.pokemonshop.activity.customer.MainActivity;
import com.example.pokemonshop.api.auth.AuthRepository;
import com.example.pokemonshop.api.auth.AuthService;
import com.example.pokemonshop.model.LoginResponse;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.gms.common.SignInButton;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvNotAccountYet;
    private Button btnSignIn;
    private SignInButton btnGoogleLogin;

    private final String REQUIRE = "Không để trống";
    private static final int GOOGLE_SIGN_IN = 1001;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.edtU);
        etPassword = findViewById(R.id.edtP);
        tvNotAccountYet = findViewById(R.id.tvAY);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin); // <-- Button mới

        // Đăng nhập truyền thống
        btnSignIn.setOnClickListener(v -> {
            String email = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            loginUser(email, password);
        });

        // Đăng ký
        tvNotAccountYet.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Google Sign-in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogleLogin.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, GOOGLE_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            Log.d("GOOGLE_SIGN_IN", "Result code: " + resultCode);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    Log.d("GOOGLE_SIGN_IN", "Account email: " + account.getEmail());
                    String idToken = account.getIdToken();
                    if (idToken != null) {
                        Log.d("ID_TOKEN", idToken);
                        sendIdTokenToBackend(idToken);
                    } else {
                        Log.e("GOOGLE_SIGN_IN", "ID Token is null");
                        Toast.makeText(this, "Không lấy được ID Token", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("GOOGLE_SIGN_IN", "Account is null");
                    Toast.makeText(this, "Không lấy được thông tin tài khoản", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Log.e("GOOGLE_SIGN_IN", "ApiException: " + e.getStatusCode() + " - " + e.getMessage());
                String errorMessage = "Lỗi Google Sign-In: ";
                switch (e.getStatusCode()) {
                    case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                        errorMessage += "Đăng nhập bị hủy";
                        break;
                    case GoogleSignInStatusCodes.NETWORK_ERROR:
                        errorMessage += "Lỗi mạng";
                        break;
                    case GoogleSignInStatusCodes.INVALID_ACCOUNT:
                        errorMessage += "Tài khoản không hợp lệ";
                        break;
                    case GoogleSignInStatusCodes.SIGN_IN_REQUIRED:
                        errorMessage += "Yêu cầu đăng nhập";
                        break;
                    default:
                        errorMessage += e.getMessage();
                        break;
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendIdTokenToBackend(String idToken) {
        AuthService authService = AuthRepository.getAuthService();
        JsonObject json = new JsonObject();
        json.addProperty("idToken", idToken);

        Log.d("GOOGLE_LOGIN", "Sending ID token to backend: " + idToken.substring(0, 50) + "...");

        Call<LoginResponse> call = authService.loginWithGoogle(json);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d("GOOGLE_LOGIN", "Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("GOOGLE_LOGIN", "Login successful");
                    handleToken(response.body().getAccessToken());
                } else {
                    Log.e("GOOGLE_LOGIN", "Error response: " + response.code() + " - " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string()
                                : "Unknown error";
                        Log.e("GOOGLE_LOGIN", "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("GOOGLE_LOGIN", "Could not read error body", e);
                    }
                    Toast.makeText(LoginActivity.this, "Lỗi xác thực Google: " + response.code(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("GOOGLE_LOGIN", "Network error", t);
                Toast.makeText(LoginActivity.this, "Lỗi kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String password) {
        if (!checkInput())
            return;

        AuthService authService = AuthRepository.getAuthService();
        Call<LoginResponse> call = authService.login(email, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleToken(response.body().getAccessToken());
                } else {
                    Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleToken(String accessToken) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("accessToken", accessToken);
        editor.apply();

        try {
            String[] decodedParts = JWTUtils.decoded(accessToken);
            String body = decodedParts[1];
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            String role = json.get("Role").getAsString();

            Intent intent = null;
            if ("ADMIN".equals(role)) {
                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, MainActivity.class);
                if (json.has("CustomerId")) {
                    int customerId = json.get("CustomerId").getAsInt();
                    editor.putInt("customerId", customerId);
                    editor.apply();
                }
            }
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Không thể xử lý token", Toast.LENGTH_SHORT).show();
        }
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
    }//fdsfsdfsd
}
