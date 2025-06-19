package com.example.pokemonshop.activity.admin;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.example.pokemonshop.R;
import com.example.pokemonshop.adapters.CategoryAdapter;
import com.example.pokemonshop.api.Category.CategoryRepository;
import com.example.pokemonshop.api.Category.CategoryService;
import com.example.pokemonshop.model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    // Khai báo ListView để hiển thị danh sách các danh mục
    private ListView listViewCategory;
    // FloatingActionButton để thêm danh mục mới
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_admin);

        // Đặt padding cho view chính dựa trên Insets của hệ thống (ví dụ: thanh trạng thái)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo và thiết lập Toolbar cho Activity
        Toolbar toolbar = findViewById(R.id.toolbarCategoryAdminHome);
        toolbar.setTitle("Các danh mục");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {
            // Xử lý sự kiện khi nhấn nút quay lại trên Toolbar
            Intent intent = new Intent(CategoryActivity.this, AdminDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            CategoryActivity.this.finish();
        });

        // Khởi tạo ListView và FloatingActionButton
        listViewCategory = findViewById(R.id.listViewCategory);
        fab = findViewById(R.id.fab);

        // Xử lý sự kiện khi nhấn vào FloatingActionButton để thêm danh mục mới
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
                startActivity(intent);
            }
        });

        CategoryService cateService = CategoryRepository.getCategoryService();
        Call<List<Category>> call = cateService.getAllCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                // Xử lý khi nhận được phản hồi thành công từ API
                if (!response.isSuccessful()) {
                    Log.e("CategoryActivity", "Lỗi: " + response.code());
                    Toast.makeText(CategoryActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Category> categories = response.body();
                if (categories == null || categories.isEmpty()) {
                    Log.e("CategoryActivity", "Không nhận được dữ liệu");
                    Toast.makeText(CategoryActivity.this, "Không có dữ liệu", Toast.LENGTH_LONG).show();
                    return;
                }

                // Thiết lập adapter cho ListView để hiển thị danh sách danh mục
                listViewCategory.setAdapter(new CategoryAdapter(categories, getApplicationContext()));

                // Xử lý sự kiện khi người dùng nhấn vào một danh mục trong ListView
                listViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Category category = (Category) parent.getItemAtPosition(position);
                        Intent intent = new Intent(CategoryActivity.this, CategoryDetailActivity.class);
                        intent.putExtra("category", category);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                // Xử lý khi thất bại trong việc tải dữ liệu từ API
                Log.e("CategoryActivity", "Failed to load data: " + t.getMessage(), t);
                Toast.makeText(CategoryActivity.this, "Tải dữ liệu thất bại: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
