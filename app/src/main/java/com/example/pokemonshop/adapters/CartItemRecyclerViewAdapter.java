package com.example.pokemonshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pokemonshop.R;
import com.example.pokemonshop.activity.customer.ProductDetailActivity;
import com.example.pokemonshop.activity.customer.fragments.CartFragment;
import com.example.pokemonshop.api.CartItem.CartItemRepository;
import com.example.pokemonshop.api.CartItem.CartItemService;
import com.example.pokemonshop.api.Product.ProductRepository;
import com.example.pokemonshop.api.Product.ProductService;
import com.example.pokemonshop.model.CartItem;
import com.example.pokemonshop.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartItemRecyclerViewAdapter extends RecyclerView.Adapter<CartItemRecyclerViewAdapter.ViewHolder> {
    private List<CartItem> items; // Danh sách các mặt hàng trong giỏ hàng
    private Context context; // Context của ứng dụng
    private ProductService productService; // Service để lấy thông tin sản phẩm
    private CartItemService cartItemService; // Service để thao tác với giỏ hàng
    private CartFragment cartFragment; // Fragment giỏ hàng để cập nhật lại giá trị khi xóa sản phẩm

    // Constructor, khởi tạo adapter với các tham số như danh sách các mặt hàng, context, và fragment giỏ hàng
    public CartItemRecyclerViewAdapter(List<CartItem> items, Context context, CartFragment cartFragment) {
        this.items = items; // Danh sách các mặt hàng
        this.context = context; // Context để bắt đầu các Intent
        this.cartFragment = cartFragment; // Fragment giỏ hàng để cập nhật tổng giá trị khi thay đổi
        this.cartItemService = CartItemRepository.getCartItemService(); // Khởi tạo CartItemService từ repository
        this.productService = ProductRepository.getProductService(); // Khởi tạo ProductService từ repository
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho mỗi item trong RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view); // Trả về ViewHolder để sử dụng trong onBindViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy mặt hàng hiện tại từ danh sách
        CartItem cart = items.get(position);

        // Cập nhật các thông tin về tên sản phẩm, giá và số lượng vào các TextView, EditText
        holder.textViewName.setText(cart.getProductVIew().getName());
        holder.textViewPrice.setText(String.format("%.2f VND", cart.getProductVIew().getPrice()));
        holder.quantityEditText.setText(String.valueOf(cart.getQuantity()));

        // Hiển thị hình ảnh sản phẩm, mặc định là hình Pikachu
        holder.imageView.setImageResource(R.drawable.pikachu);

        // Xử lý sự kiện click vào item để xem chi tiết sản phẩm
        holder.itemView.setOnClickListener(v -> viewProductDetails(cart.getProductVIew().getProductId()));

        // Xử lý sự kiện nhấn nút "Xóa" để xóa sản phẩm khỏi giỏ hàng
        holder.btnDelete.setOnClickListener(v -> deleteCartItem(cart.getItemId(), position));
    }

    @Override
    public int getItemCount() {
        return items.size(); // Trả về số lượng item trong giỏ hàng
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

    // Phương thức xóa sản phẩm khỏi giỏ hàng
    public void deleteCartItem(int itemId, int position) {
        Call<Void> call = cartItemService.deleteItem(itemId); // Gọi API để xóa sản phẩm khỏi giỏ hàng
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Nếu xóa thành công
                if (response.isSuccessful()) {
                    items.remove(position); // Xóa item khỏi danh sách
                    notifyItemRemoved(position); // Cập nhật lại RecyclerView
                    notifyItemRangeChanged(position, items.size()); // Cập nhật lại các item còn lại
                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    cartFragment.updateTotalPrice(); // Cập nhật tổng giá trị trong giỏ hàng
                } else {
                    // Nếu xóa thất bại
                    Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Nếu gọi API xóa thất bại
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ViewHolder để tái sử dụng các view trong RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName; // TextView hiển thị tên sản phẩm
        public TextView textViewPrice; // TextView hiển thị giá sản phẩm
        public ImageView imageView; // ImageView hiển thị ảnh sản phẩm
        public EditText quantityEditText; // EditText hiển thị số lượng sản phẩm
        public AppCompatButton btnDelete; // Button để xóa sản phẩm khỏi giỏ hàng

        // Khởi tạo các thành phần giao diện
        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.tvItemName);
            textViewPrice = itemView.findViewById(R.id.tvItemPrice);
            quantityEditText = itemView.findViewById(R.id.tvItemQuantity);
            imageView = itemView.findViewById(R.id.imgItem);
            btnDelete = itemView.findViewById(R.id.btnDeleteItemCart);
        }
    }
}
