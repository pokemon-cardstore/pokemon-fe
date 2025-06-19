package com.example.pokemonshop.adapters;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.pokemonshop.R;
import com.example.pokemonshop.model.Product;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {
    private List<Product> products;
    private Context context;

    public ProductAdapter(List<Product> products, Context context) {
        super(context, R.layout.category_row_layout, products);
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Tạo ra LayoutInflater để "inflate" (tạo) View từ file XML layout
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Inflate layout cho mỗi item của ListView (ở đây sử dụng category_row_layout)
        convertView = layoutInflater.inflate(R.layout.category_row_layout, parent, false);

        // Lấy đối tượng Product tại vị trí position
        Product product = products.get(position);

        // Tìm TextView trong layout và đặt tên sản phẩm vào
        TextView textViewName = convertView.findViewById(R.id.textViewName);
        textViewName.setText(product.getName()); // Đặt tên sản phẩm vào TextView

        // Tìm ImageView trong layout để đặt hình ảnh sản phẩm
        ImageView imageView = convertView.findViewById(R.id.category_image_admin);
        // Kiểm tra xem sản phẩm có hình ảnh không
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String base64Image = product.getImages().get(0).getBase64StringImage(); // Lấy hình ảnh đầu tiên từ danh sách hình ảnh
            if (base64Image != null && !base64Image.isEmpty()) {
                // Giải mã chuỗi base64 thành mảng byte
                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Glide.with(context)
                        .load(imageBytes)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.pikachu);
            }
        } else {
            imageView.setImageResource(R.drawable.pikachu);
        }

        return convertView;
    }
}
