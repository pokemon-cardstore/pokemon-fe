package com.example.pokemonshop.activity.customer.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonshop.R;
import com.example.pokemonshop.activity.auth.JWTUtils;
import com.example.pokemonshop.activity.customer.MainActivity;
import com.example.pokemonshop.activity.customer.OrderCustomerDetailActivity;
import com.example.pokemonshop.adapters.CartItemRecyclerViewAdapter;
import com.example.pokemonshop.api.CartItem.CartItemRepository;
import com.example.pokemonshop.api.CartItem.CartItemService;
import com.example.pokemonshop.model.CartItem;
//import com.example.pokemonshop.utils.NotificationHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private RecyclerView itemRecyclerView;
    private TextView totalPriceTextView;
    private CartItemRecyclerViewAdapter cartAdapter;
    private int customerId;
    private List<CartItem> items;
    AppCompatButton buttonOrder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        View view = inflater.inflate(R.layout.activity_cart, container, false);

        // Khởi tạo Toolbar với tiêu đề và nút quay lại
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Giỏ hàng"); // Đặt tiêu đề cho toolbar
        toolbar.setTitleTextColor(Color.WHITE); // Đặt màu chữ tiêu đề
        toolbar.setSubtitleTextColor(Color.WHITE); // Đặt màu chữ phụ đề
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow); // Đặt biểu tượng nút quay lại
        toolbar.setNavigationOnClickListener(v -> { // Xử lý sự kiện khi bấm nút quay lại
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        // Khởi tạo RecyclerView để hiển thị các sản phẩm trong giỏ hàng
        itemRecyclerView = view.findViewById(R.id.rvCartItems);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Khởi tạo TextView để hiển thị tổng giá tiền
        totalPriceTextView = view.findViewById(R.id.totalPrice);

        // Khởi tạo nút Đặt hàng
        buttonOrder = view.findViewById(R.id.btnOrder);

        // Lấy access token từ Intent hoặc SharedPreferences
        String accessToken = getActivity().getIntent().getStringExtra("accessToken");
        if (accessToken == null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            accessToken = sharedPreferences.getString("accessToken", null);
        }

        // Giải mã access token để lấy ID khách hàng
        if (accessToken != null) {
            try {
                String[] decodedParts = JWTUtils.decoded(accessToken);
                String body = decodedParts[1];

                // Phân tích cú pháp body để lấy CustomerId
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                customerId = Integer.parseInt(jsonObject.get("CustomerId").getAsString());

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Lỗi khi giải mã token", Toast.LENGTH_SHORT).show();
            }
        }

        // Gọi hàm để tải các sản phẩm trong giỏ hàng
        loadCartItem();

        // Xử lý sự kiện khi bấm nút Đặt hàng
        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderCustomerDetailActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    // Hàm để tải các sản phẩm trong giỏ hàng
    private void loadCartItem() {
        CartItemService cartService = CartItemRepository.getCartItemService();
        Call<List<CartItem>> call = cartService.getCartFromCustomer(customerId);

        // Thực hiện gọi API để lấy danh sách sản phẩm trong giỏ hàng
        call.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful()) {
                    items = response.body();
                    // Khởi tạo adapter và gán vào RecyclerView
                    cartAdapter = new CartItemRecyclerViewAdapter(items, getContext(), CartFragment.this);
                    itemRecyclerView.setAdapter(cartAdapter);
                    // Tính tổng giá tiền
                    calculateTotalPrice(items);
                    // Hiển thị badge notification với số lượng sản phẩm trong giỏ hàng
                    //NotificationHelper.showCartBadgeNotification(getContext(), 1);
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm để tính tổng giá tiền của các sản phẩm trong giỏ hàng
    private void calculateTotalPrice(List<CartItem> items) {
        double totalPrice = 0.0;
        for (CartItem item : items) {
            totalPrice += item.getProductVIew().getPrice() * item.getQuantity();
        }
        totalPriceTextView.setText(String.format("%.2f VND", totalPrice));
    }

    // Hàm để cập nhật tổng giá tiền khi giỏ hàng thay đổi
    public void updateTotalPrice() {
        calculateTotalPrice(items);
    }
}
