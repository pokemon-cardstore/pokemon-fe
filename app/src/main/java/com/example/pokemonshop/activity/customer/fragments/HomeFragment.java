package com.example.pokemonshop.activity.customer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonshop.R;
import com.example.pokemonshop.adapters.CategoryRecyclerViewAdapter;
import com.example.pokemonshop.adapters.ProductRecyclerViewAdapter;
import com.example.pokemonshop.api.APIClient;
import com.example.pokemonshop.api.Category.CategoryService;
import com.example.pokemonshop.api.Product.ProductService;
import com.example.pokemonshop.model.Category;
import com.example.pokemonshop.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Fragment hiển thị giao diện trang chủ
public class HomeFragment extends Fragment {

    private RecyclerView categoryRecyclerView;
    private RecyclerView productRecyclerView;
    private CategoryRecyclerViewAdapter categoryAdapter;
    private ProductRecyclerViewAdapter productAdapter;
    private SearchView searchView;
    private ImageView searchIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout từ file XML để tạo view
        View view = inflater.inflate(R.layout.activity_main, container, false);

        // Tham chiếu các thành phần giao diện
        categoryRecyclerView = view.findViewById(R.id.category_list);
        productRecyclerView = view.findViewById(R.id.featured_list);
        searchView = view.findViewById(R.id.search_view);
        searchIcon = view.findViewById(R.id.ic_search);

        // Thiết lập LinearLayoutManager theo chiều ngang cho RecyclerView hiển thị danh mục
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        // Thiết lập GridLayoutManager với 2 cột cho RecyclerView hiển thị sản phẩm
        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Gọi phương thức để tải danh mục và sản phẩm
        loadCategories();
        loadAllProducts(); // Ban đầu tải tất cả sản phẩm

        // Đặt sự kiện click cho biểu tượng tìm kiếm
        searchIcon.setOnClickListener(v -> {
            String query = searchView.getQuery().toString(); // Lấy từ khóa tìm kiếm
            if (!query.isEmpty()) {
                searchProducts(query); // Gọi phương thức tìm kiếm sản phẩm
            } else {
                // Hiển thị thông báo nếu từ khóa tìm kiếm trống
                Toast.makeText(getContext(), "Vui lòng nhập từ khóa để tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });

        return view; // Trả về view đã được inflate
    }

    // Phương thức tải danh mục sản phẩm từ API
    private void loadCategories() {
        CategoryService categoryService = APIClient.getClient().create(CategoryService.class);
        Call<List<Category>> call = categoryService.getAllCategories();

        // Thực hiện gọi API bất đồng bộ
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                // Nếu phản hồi thành công và có dữ liệu
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    // Tạo adapter và gán cho RecyclerView hiển thị danh mục
                    categoryAdapter = new CategoryRecyclerViewAdapter(categories, getContext());
                    categoryRecyclerView.setAdapter(categoryAdapter);

                    // Đặt sự kiện click cho danh mục để tải sản phẩm theo danh mục
                    categoryAdapter.setOnCategoryClickListener(category -> loadProductsByCategory(category.getCategoryId()));
                } else {
                    // Hiển thị thông báo nếu không thể tải danh mục
                    Toast.makeText(getContext(), "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                // Hiển thị thông báo lỗi khi gọi API thất bại
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllProducts() {
        // Tạo instance của ProductService để gọi API
        ProductService productService = APIClient.getClient().create(ProductService.class);
        // Gọi API để lấy danh sách tất cả sản phẩm
        retrofit2.Call<List<Product>> call = productService.getAllProducts();

        // Thực hiện cuộc gọi API bất đồng bộ
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Product>> call, Response<List<Product>> response) {
                // Kiểm tra nếu phản hồi thành công và có dữ liệu trả về
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    // Tạo adapter cho danh sách sản phẩm và gán cho RecyclerView
                    productAdapter = new ProductRecyclerViewAdapter(products, getContext());
                    productRecyclerView.setAdapter(productAdapter);
                } else {
                    // Hiển thị thông báo lỗi nếu không thể tải sản phẩm
                    Toast.makeText(getContext(), "Không thể tải sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Product>> call, Throwable t) {
                // Hiển thị thông báo lỗi nếu có lỗi trong quá trình gọi API
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductsByCategory(int categoryId) {
        // Tạo instance của ProductService để gọi API lấy sản phẩm theo danh mục
        ProductService productService = APIClient.getClient().create(ProductService.class);
        retrofit2.Call<List<Product>> call = productService.getProductsByCategory(categoryId);

        // Thực hiện cuộc gọi API bất đồng bộ
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Product>> call, Response<List<Product>> response) {
                // Kiểm tra nếu phản hồi thành công và có dữ liệu trả về
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    // Kiểm tra nếu không có sản phẩm trong danh mục, tải tất cả sản phẩm
                    if (products.isEmpty()) {
                        loadAllProducts(); // Gọi phương thức để tải tất cả sản phẩm
                    } else {
                        // Tạo adapter cho sản phẩm theo danh mục và gán cho RecyclerView
                        productAdapter = new ProductRecyclerViewAdapter(products, getContext());
                        productRecyclerView.setAdapter(productAdapter);
                    }
                } else {
                    // Nếu không thể tải sản phẩm theo danh mục, tải tất cả sản phẩm
                    loadAllProducts();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Product>> call, Throwable t) {
                // Hiển thị thông báo lỗi nếu có lỗi trong quá trình gọi API
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchProducts(String query) {
        // Tạo instance của ProductService để gọi API tìm kiếm sản phẩm
        ProductService productService = APIClient.getClient().create(ProductService.class);
        retrofit2.Call<List<Product>> call = productService.searchProducts(query);

        // Thực hiện cuộc gọi API bất đồng bộ
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Product>> call, Response<List<Product>> response) {
                // Kiểm tra nếu phản hồi thành công và có dữ liệu trả về
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();
                    // Tạo adapter cho sản phẩm tìm kiếm và gán cho RecyclerView
                    productAdapter = new ProductRecyclerViewAdapter(products, getContext());
                    productRecyclerView.setAdapter(productAdapter);
                } else {
                    // Hiển thị thông báo nếu không tìm thấy sản phẩm
                    Toast.makeText(getContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Product>> call, Throwable t) {
                // Hiển thị thông báo lỗi nếu có lỗi trong quá trình gọi API
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
