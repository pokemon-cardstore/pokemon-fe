package com.example.pokemonshop.activity.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonshop.R;
import com.example.pokemonshop.activity.auth.JWTUtils;
import com.example.pokemonshop.adapters.OrderCustomerDetailRecycleViewAdapter;
import com.example.pokemonshop.api.CartItem.CartItemRepository;
import com.example.pokemonshop.api.CartItem.CartItemService;
import com.example.pokemonshop.api.order.OrderRepository;
import com.example.pokemonshop.api.order.OrderService;
import com.example.pokemonshop.model.CartItem;
import com.example.pokemonshop.model.OrderRequestDto;
import com.example.pokemonshop.model.VNPayUrl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderCustomerDetailActivity extends AppCompatActivity {
    private RecyclerView itemRecyclerView;
    private OrderCustomerDetailRecycleViewAdapter orderAdapter;
    private List<CartItem> items;
    private int customerId;

    AppCompatButton buttonOrder;
    private TextView totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bật chế độ Edge to Edge để giao diện chiếm toàn bộ màn hình
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_customer_detail);

        // Áp dụng padding cho giao diện để tránh bị che khuất bởi thanh hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Thiết lập Toolbar với tiêu đề "Chi tiết đơn hàng"
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Chi tiết đơn hàng");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {
            // Điều hướng về MainActivity khi nhấn vào nút quay lại
            Intent intent = new Intent(OrderCustomerDetailActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            OrderCustomerDetailActivity.this.finish();
        });

        // Khởi tạo các thành phần UI
        totalPriceTextView = findViewById(R.id.totalPrice);
        itemRecyclerView = findViewById(R.id.orders_recycler_view);
        buttonOrder = findViewById(R.id.btnOrder);

        // Thiết lập sự kiện cho nút "Đặt hàng"
        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<OrderRequestDto> cartItems = new ArrayList<>();
                // Chuyển các sản phẩm trong giỏ hàng thành đối tượng OrderRequestDto
                for (CartItem item : items) {
                    OrderRequestDto orderProduct = new OrderRequestDto();
                    orderProduct.setItemId(item.getItemId());
                    orderProduct.setCustomerId(item.getCustomerId());
                    orderProduct.setProductId(item.getProductId());
                    orderProduct.setQuantity(item.getQuantity());
                    cartItems.add(orderProduct);
                }

                // Gửi yêu cầu tạo đơn hàng tới API
                OrderService orderService = OrderRepository.getOrderService();
                Call<VNPayUrl> call = orderService.createOrder(cartItems);
                call.enqueue(new Callback<VNPayUrl>() {
                    @Override
                    public void onResponse(Call<VNPayUrl> call, Response<VNPayUrl> response) {
                        if (response.isSuccessful()) {
                            // Nhận URL thanh toán nếu tạo đơn hàng thành công
                            VNPayUrl paymentUrl = response.body();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(paymentUrl.getUrl()));
                            startActivity(intent);
                            finish();
                        } else {
                            try {
                                // Log lỗi nếu tạo đơn hàng thất bại
                                String errorBody = response.errorBody().string();
                                Log.e("OrderError", "Error: " + errorBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(OrderCustomerDetailActivity.this, "Order fail", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<VNPayUrl> call, Throwable t) {
                        // Hiển thị lỗi khi có sự cố kết nối
                        Toast.makeText(OrderCustomerDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Lấy access token từ Intent hoặc SharedPreferences
        String accessToken = getIntent().getStringExtra("accessToken");
        if (accessToken == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            accessToken = sharedPreferences.getString("accessToken", null);
        }

        // Giải mã JWT để lấy customerId
        if (accessToken != null) {
            try {
                String[] decodedParts = JWTUtils.decoded(accessToken);
                String body = decodedParts[1];
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to decode token", Toast.LENGTH_SHORT).show();
            }
        }

        // Thiết lập RecyclerView để hiển thị danh sách giỏ hàng
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(OrderCustomerDetailActivity.this, LinearLayoutManager.VERTICAL, false));

        // Lấy dữ liệu giỏ hàng từ API
        CartItemService cartService = CartItemRepository.getCartItemService();
        Call<List<CartItem>> call = cartService.getCartFromCustomer(customerId);
        call.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful()) {
                    items = response.body();
                    // Hiển thị các sản phẩm trong RecyclerView
                    orderAdapter = new OrderCustomerDetailRecycleViewAdapter(items, OrderCustomerDetailActivity.this);
                    itemRecyclerView.setAdapter(orderAdapter);
                    // Tính tổng tiền đơn hàng
                    calculateTotalPrice(items);
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                // Hiển thị lỗi nếu không lấy được dữ liệu giỏ hàng
                Toast.makeText(OrderCustomerDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm tính tổng giá trị đơn hàng
    private void calculateTotalPrice(List<CartItem> items) {
        double totalPrice = 0.0;
        for (CartItem item : items) {
            totalPrice += item.getProductVIew().getPrice() * item.getQuantity();
        }
        // Cập nhật tổng tiền lên TextView
        totalPriceTextView.setText(String.format("%.2f VND", totalPrice));
    }
}
