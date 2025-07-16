package com.example.pokemonshop.activity.auth;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokemonshop.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ActivateWebViewActivity extends AppCompatActivity {

    private WebView webView;
    private static final String TAG = "ActivateWebView";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_activate);

        webView = findViewById(R.id.webViewActivate);

        String email = getIntent().getStringExtra("email");

        if (email != null && !email.isEmpty()) {
            try {
                String encodedEmail = URLEncoder.encode(email, "UTF-8");
                String url = "http://10.0.2.2:5275/api/activate?email=" + encodedEmail;
                Log.d(TAG, "Loading URL: " + url);

                WebSettings settings = webView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setDomStorageEnabled(true);
                settings.setSupportZoom(true);
                settings.setBuiltInZoomControls(true);
                settings.setDisplayZoomControls(false);

                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        Toast.makeText(ActivateWebViewActivity.this,
                                "Lỗi tải trang: " + error.getDescription(),
                                Toast.LENGTH_LONG).show();
                        Log.e(TAG, "WebView Error: " + error.getDescription());
                    }
                });

                webView.loadUrl(url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi mã hóa email", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Email null hoặc rỗng");
        }
    }
}
