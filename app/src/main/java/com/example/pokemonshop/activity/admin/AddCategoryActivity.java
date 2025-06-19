package com.example.pokemonshop.activity.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pokemonshop.R;
import com.example.pokemonshop.api.APIClient;
import com.example.pokemonshop.api.Category.CategoryService;
import com.example.pokemonshop.model.Category;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCategoryActivity extends AppCompatActivity {

    // Khai báo các thành phần giao diện
    private Button buttonSave; // Nút lưu danh mục mới
    private EditText edName, edDescription; // Các trường nhập liệu cho tên và mô tả danh mục

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Kích hoạt chế độ EdgeToEdge cho trải nghiệm toàn màn hình
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);

        // Đặt padding cho view chính dựa vào Insets của hệ thống (ví dụ: thanh trạng thái)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo và thiết lập Toolbar cho Activity
        Toolbar toolbar = findViewById(R.id.toolbarAddCategoryAdmin);
        toolbar.setTitle("Thêm danh mục");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {
            // Xử lý sự kiện khi nhấn nút quay lại trên Toolbar
            Intent intent = new Intent(AddCategoryActivity.this, CategoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            AddCategoryActivity.this.finish();
        });

        // Khởi tạo các trường nhập liệu và nút lưu
        edName = findViewById(R.id.editTextName);
        edDescription = findViewById(R.id.editTextDes);
        buttonSave = findViewById(R.id.btnAdd);

        // Xử lý sự kiện khi nhấn nút lưu danh mục mới
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Tạo một đối tượng Category mới từ thông tin người dùng nhập
                    Category cate = new Category();
                    cate.setName(edName.getText().toString());
                    cate.setDescription(edDescription.getText().toString());

                    // Gọi API để lưu danh mục mới
                    CategoryService cateService = APIClient.getClient().create(CategoryService.class);
                    Call<Void> call = cateService.create(cate);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            // Xử lý khi thêm danh mục thành công
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(AddCategoryActivity.this, CategoryActivity.class);
                                Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            // Xử lý khi thêm danh mục thất bại
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    // Xử lý khi có lỗi xảy ra trong quá trình tạo danh mục
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    }
