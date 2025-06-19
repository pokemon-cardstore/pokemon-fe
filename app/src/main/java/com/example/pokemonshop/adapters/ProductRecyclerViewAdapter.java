package com.example.pokemonshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Thư viện dùng để load hình ảnh
import com.example.pokemonshop.R;
 // import com.example.pokemonshop.activity.customer.ProductDetailActivity; // Activity để hiển thị chi tiết sản phẩm
import com.example.pokemonshop.activity.customer.ProductDetailActivity;
import com.example.pokemonshop.model.Product; // Model dữ liệu sản phẩm

import java.util.List;

// Adapter cho RecyclerView để hiển thị danh sách sản phẩm
public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder> {

    // Danh sách sản phẩm và context của ứng dụng
    private List<Product> products;
    private Context context;

    // Constructor để khởi tạo adapter với danh sách sản phẩm và context
    public ProductRecyclerViewAdapter(List<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_product để tạo view cho từng item trong danh sách
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view); // Trả về ViewHolder chứa view vừa tạo
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy sản phẩm từ danh sách tại vị trí hiện tại
        Product product = products.get(position);

        // Set tên và giá cho TextView
        holder.textViewName.setText(product.getName());
        holder.textViewPrice.setText(String.format("%.2f VND", product.getPrice()));
        holder.imageView.setImageResource(R.drawable.pikachu);

        // Đặt sự kiện click cho từng item trong RecyclerView
        holder.itemView.setOnClickListener(v -> {
            // Tạo Intent để mở ProductDetailActivity và truyền đối tượng product
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", product); // Truyền sản phẩm qua Intent
            context.startActivity(intent); // Bắt đầu Activity
        });
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng sản phẩm trong danh sách
        return products.size();
    }

    // Lớp ViewHolder để nắm giữ các view của từng item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName; // TextView để hiển thị tên sản phẩm
        public TextView textViewPrice; // TextView để hiển thị giá sản phẩm
        public ImageView imageView; // ImageView để hiển thị hình ảnh sản phẩm

        public ViewHolder(View itemView) {
            super(itemView);
            // Tham chiếu đến các view trong layout item_product
            textViewName = itemView.findViewById(R.id.product_name);
            textViewPrice = itemView.findViewById(R.id.product_price);
            imageView = itemView.findViewById(R.id.product_image);
        }
    }
}

