package com.example.pokemonshop.activity.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.pokemonshop.R;
import com.example.pokemonshop.activity.auth.JWTUtils;
import com.example.pokemonshop.api.CartItem.CartItemRepository;
import com.example.pokemonshop.api.CartItem.CartItemService;
import com.example.pokemonshop.model.CartItem;
import com.example.pokemonshop.model.Product;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    // Khai báo các thành phần giao diện
    private TextView productNameTextView;
    private TextView productDescriptionTextView;
    private TextView productPriceTextView;
    private TextView productStatusTextView;
    private EditText quantityTextView;
    private Button buttonAddToCart;
    private ImageView productImageView;
    private Product product; // Đối tượng sản phẩm hiện tại
    private int customerId; // ID của khách hàng
    private final String REQUIRE = "Require"; // Thông báo yêu cầu nhập liệu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_homepage);

        // Thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed()); // Xử lý sự kiện quay lại khi nhấn vào toolbar

        // Tham chiếu các thành phần giao diện từ layout
        productNameTextView = findViewById(R.id.product_name);
        productDescriptionTextView = findViewById(R.id.product_description);
        productPriceTextView = findViewById(R.id.product_price);
        productStatusTextView = findViewById(R.id.product_status);
        quantityTextView = findViewById(R.id.number_edit_text);
        productImageView = findViewById(R.id.product_image);

        // Lấy đối tượng sản phẩm từ Intent
        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");
        if (product != null) {
            // Hiển thị thông tin sản phẩm trên các TextView
            productNameTextView.setText(product.getName());
            productDescriptionTextView.setText(product.getDescription());
            productPriceTextView.setText(String.format("%.2f VND", product.getPrice()));
            productStatusTextView.setText(product.getQuantity() > 0 ? "Sản phẩm còn hàng" : "Sản phẩm đã hết hàng");
            productStatusTextView.setTextColor(getResources().getColor(product.getQuantity() > 0 ? R.color.green : R.color.red));

            // Hiển thị hình ảnh sản phẩm bằng Glide
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                String base64Image = product.getImages().get(0).getBase64StringImage();
                if (base64Image != null && !base64Image.isEmpty()) {
                    byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                    Glide.with(this)
                            .load(imageBytes) // Load hình ảnh từ byte array
                            .into(productImageView); // Gán hình ảnh vào ImageView
                } else {
                    productImageView.setImageResource(R.drawable.pikachu); // Hình ảnh mặc định
                }
            } else {
                productImageView.setImageResource(R.drawable.pikachu); // Hình ảnh mặc định nếu không có ảnh
            }
        }

        // Xử lý sự kiện khi người dùng nhấn vào nút "Add to Cart"
        buttonAddToCart = findViewById(R.id.btnAddToCart);
        buttonAddToCart.setOnClickListener(v -> {
            if (!checkInput()) { // Kiểm tra đầu vào
                return;
            }
            try {
                // Tạo một CartItem mới và thiết lập các giá trị
                CartItem item = new CartItem();
                item.setProductId(product.getProductId());
                item.setCustomerId(customerId);
                item.setQuantity(Integer.parseInt(quantityTextView.getText().toString()));
                // Kiểm tra số lượng chọn không vượt quá số lượng tồn kho
                if (item.getQuantity() > product.getQuantity()) {
                    Toast.makeText(getApplicationContext(), "Không đủ số lượng sản phẩm bạn yêu cầu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gọi API để thêm sản phẩm vào giỏ hàng
                CartItemService cartItemService = CartItemRepository.getCartItemService();
                Call<Void> call = cartItemService.AddToCart(item);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Thêm sản phẩm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Thêm sản phẩm vào giỏ hàng thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Lấy accessToken từ Intent hoặc SharedPreferences
        String accessToken = getIntent().getStringExtra("accessToken");
        if (accessToken == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            accessToken = sharedPreferences.getString("accessToken", null);
        }
        if (accessToken != null) {
            try {
                // Giải mã JWT để lấy thông tin khách hàng
                String[] decodedParts = JWTUtils.decoded(accessToken);
                String body = decodedParts[1];

                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString()); // Lấy ID khách hàng
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ProductDetailActivity.this, "Failed to decode token", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Phương thức kiểm tra đầu vào
    private boolean checkInput() {
        if (TextUtils.isEmpty(quantityTextView.getText().toString())) {
            quantityTextView.setError(REQUIRE); // Hiển thị lỗi nếu ô nhập liệu trống
            return false;
        }
        if (Integer.parseInt(quantityTextView.getText().toString()) <= 0) {
            quantityTextView.setError(REQUIRE); // Hiển thị lỗi nếu số lượng <= 0
            return false;
        }
        return true; // Đầu vào hợp lệ
    }
}
