package com.example.pokemonshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pokemonshop.R;
import com.example.pokemonshop.activity.customer.ProductDetailActivity;
import com.example.pokemonshop.api.Product.ProductRepository;
import com.example.pokemonshop.api.Product.ProductService;
import com.example.pokemonshop.model.CartItem;
import com.example.pokemonshop.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderCustomerDetailRecycleViewAdapter extends RecyclerView.Adapter<OrderCustomerDetailRecycleViewAdapter.ViewHolder> {
    private List<CartItem> items; // Danh sách các mặt hàng trong đơn hàng
    private Context context; // Context của ứng dụng
    private ProductService productService; // Service để lấy thông tin sản phẩm

    // Constructor khởi tạo adapter với danh sách các mặt hàng và context
    public OrderCustomerDetailRecycleViewAdapter(List<CartItem> items, Context context) {
        this.items = items; // Danh sách các mặt hàng trong đơn hàng
        this.context = context; // Context để mở các Activity mới
        this.productService = ProductRepository.getProductService(); // Khởi tạo ProductService từ repository
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho mỗi item trong RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.order_customer_item, parent, false);
        return new ViewHolder(view); // Trả về ViewHolder để sử dụng trong onBindViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy mặt hàng hiện tại từ danh sách
        CartItem cart = items.get(position);

        // Cập nhật các thông tin về tên sản phẩm, giá và số lượng vào các TextView
        holder.textViewName.setText(cart.getProductVIew().getName());
        holder.textViewPrice.setText(String.format("%.2f VND", cart.getProductVIew().getPrice()));
        holder.quantityView.setText(String.valueOf(cart.getQuantity()));

        // Hiển thị hình ảnh sản phẩm, mặc định là hình Pikachu
        holder.imageView.setImageResource(R.drawable.pikachu);

        // Xử lý sự kiện click vào item để xem chi tiết sản phẩm
        holder.itemView.setOnClickListener(v -> viewProductDetails(cart.getProductVIew().getProductId()));
    }

    @Override
    public int getItemCount() {
        return items.size(); // Trả về số lượng item trong đơn hàng
    }

    // Phương thức xem chi tiết sản phẩm khi người dùng nhấn vào item
    private void viewProductDetails(int productId) {
        Call<Product> call = productService.find(productId); // Gọi API để lấy chi tiết sản phẩm
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                // Nếu API trả về thành công
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body(); // Lấy thông tin sản phẩm từ response
                    Intent intent = new Intent(context, ProductDetailActivity.class); // Tạo Intent để mở ProductDetailActivity
                    if (product.getImages() != null && !product.getImages().isEmpty()) {
                        // Nếu có ảnh, chuyển ảnh vào Intent
                        intent.putExtra("productImage", product.getImages().get(0).getBase64StringImage());
                    }
                    intent.putExtra("product", product); // Truyền đối tượng sản phẩm vào Intent
                    context.startActivity(intent); // Bắt đầu Activity chi tiết sản phẩm
                } else {
                    // Nếu không thành công, hiển thị thông báo lỗi
                    Toast.makeText(context, "Failed to load product details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                // Nếu gọi API thất bại
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ViewHolder để tái sử dụng các view trong RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName; // TextView hiển thị tên sản phẩm
        public TextView textViewPrice; // TextView hiển thị giá sản phẩm
        public ImageView imageView; // ImageView hiển thị ảnh sản phẩm
        public TextView quantityView; // TextView hiển thị số lượng sản phẩm

        // Khởi tạo các thành phần giao diện
        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.tvItemName);
            textViewPrice = itemView.findViewById(R.id.tvItemPrice);
            quantityView = itemView.findViewById(R.id.tvItemQuantity);
            imageView = itemView.findViewById(R.id.imgItem);
        }
    }
}
