package com.example.pokemonshop.activity.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pokemonshop.R;
import com.example.pokemonshop.api.APIClient;
import com.example.pokemonshop.api.Category.CategoryRepository;
import com.example.pokemonshop.api.Category.CategoryService;
import com.example.pokemonshop.model.Category;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDetailActivity extends AppCompatActivity {

    private Button buttonEdit, buttonDelete;

    private TextView textViewId, textViewName, textViewDes;

    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Kích hoạt chế độ hiển thị toàn màn hình
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_detail);
        // Đặt padding cho các thanh hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Thiết lập toolbar với tiêu đề và nút quay lại
        Toolbar toolbar = findViewById(R.id.toolbarCategoryAdmin);
        toolbar.setTitle("Chi tiết danh mục");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(CategoryDetailActivity.this, CategoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            CategoryDetailActivity.this.finish();
        });

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Khởi tạo các TextView
        textViewName = (TextView) findViewById(R.id.editName);
        textViewDes = (TextView) findViewById(R.id.editDescription);
        textViewId = findViewById(R.id.categoryId);

        // Lấy dữ liệu category từ intent
        Intent intent = getIntent();
        category = (Category) intent.getSerializableExtra("category");

        // Đặt giá trị cho các TextView
        textViewId.setText(String.valueOf(category.getCategoryId()));
        textViewName.setText(category.getName());
        textViewDes.setText(category.getDescription());

        // Thiết lập sự kiện click cho nút chỉnh sửa
        buttonEdit = findViewById(R.id.btnUpdate);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            // Thêm flag để xử lý cách intent hoạt động:
            // FLAG_ACTIVITY_CLEAR_TOP: Nếu CategoryActivity đã có trong ngăn xếp,
            // nó sẽ được đưa lên đầu và tất cả các activity phía trên nó sẽ bị xóa.
            // FLAG_ACTIVITY_NEW_TASK: Đảm bảo CategoryActivity mở ở đầu ngăn xếp,
            // và yêu cầu hệ thống khởi chạy trong một task mới nếu cần.
            @Override
            public void onClick(View v) {
                try {
                    // Tạo đối tượng category mới với dữ liệu đã cập nhật
                    Category cate = new Category();
                    cate.setName(textViewName.getText().toString());
                    cate.setDescription(textViewDes.getText().toString());
                    // Gọi API để cập nhật category
                    CategoryService cateService = APIClient.getClient().create(CategoryService.class);
                    Call<Void> call = cateService.update(cate, category.getCategoryId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                // Hiển thị thông báo thành công và quay lại danh sách category
                                Intent intent = new Intent(CategoryDetailActivity.this, CategoryActivity.class);
                                Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            // Hiển thị thông báo lỗi
                            Toast.makeText(getApplicationContext(), "Thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    // Hiển thị thông báo ngoại lệ
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Thiết lập sự kiện click cho nút xóa
        buttonDelete = findViewById(R.id.btnDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Hiển thị hộp thoại xác nhận trước khi xóa category
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Xác nhận");
                    builder.setMessage("Bạn chắc chưa?");
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Gọi API để xóa category
                            CategoryService cateService = CategoryRepository.getCategoryService();
                            Call<Void> call = cateService.delete(category.getCategoryId());
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        // Hiển thị thông báo thành công và quay lại danh sách category
                                        Intent intent = new Intent(CategoryDetailActivity.this, CategoryActivity.class);
                                        Toast.makeText(getApplicationContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    // Hiển thị thông báo lỗi
                                    Toast.makeText(getApplicationContext(), "Thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Hủy bỏ việc xóa
                            dialog.cancel();
                        }
                    });
                    builder.create().show();

                } catch (Exception e) {
                    // Hiển thị thông báo ngoại lệ
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
