package com.example.pokemonshop.activity.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.pokemonshop.R;
import com.example.pokemonshop.api.APIClient;
import com.example.pokemonshop.api.Category.CategoryRepository;
import com.example.pokemonshop.api.Category.CategoryService;
import com.example.pokemonshop.api.Product.ProductRepository;
import com.example.pokemonshop.api.Product.ProductService;
import com.example.pokemonshop.model.Category;
import com.example.pokemonshop.model.Product;
import com.example.pokemonshop.model.Product.ProductImage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button btnEdit, btnDelete;
    private EditText productName, productDes, productPrice, productQuantity;
    private ImageView imageView;
    private Spinner spinner;
    private Product product;
    private List<Category> categoryList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private int selectedCategoryId;
    private String base64Image = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnEdit = findViewById(R.id.btnProductUpdate);
        btnDelete = findViewById(R.id.btnProductDelete);

        productName = findViewById(R.id.editProductName);
        productDes = findViewById(R.id.editProductDescription);
        productPrice = findViewById(R.id.editProductPrice);
        productQuantity = findViewById(R.id.editProductQuantity);
        imageView = findViewById(R.id.product_image_admin_detail);
        spinner = findViewById(R.id.categorySpinner);

        Toolbar toolbar = findViewById(R.id.toolbarProductAdmin);
        toolbar.setTitle("Chi tiết sản phẩm");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, ProductActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            ProductDetailActivity.this.finish();
        });

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        product = (Product) intent.getSerializableExtra("product");

        productName.setText(product.getName());
        productDes.setText(product.getDescription());
        productPrice.setText(String.valueOf(product.getPrice()));
        productQuantity.setText(String.valueOf(product.getQuantity()));

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String base64Image = product.getImages().get(0).getBase64StringImage();
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Glide.with(ProductDetailActivity.this)
                        .load(imageBytes)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.pikachu); // Default image
            }
        } else {
            imageView.setImageResource(R.drawable.pikachu); // Default image
        }

        // Initialize the adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        getCategories(product.getCategoryId());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoryId = categoryList.get(position).getCategoryId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imageView.setOnClickListener(v -> openImageChooser());

        btnEdit.setOnClickListener(v -> {
            try {
                Product pro = new Product();
                pro.setName(productName.getText().toString());
                pro.setDescription(productDes.getText().toString());
                pro.setPrice(Double.parseDouble(productPrice.getText().toString()));
                pro.setQuantity(Integer.parseInt(productQuantity.getText().toString()));
                pro.setCategoryId(selectedCategoryId);
                pro.setProductId(product.getProductId());

                if (base64Image != null) {
                    List<ProductImage> images = new ArrayList<>();
                    images.add(new ProductImage(null, base64Image));
                    pro.setImages(images);
                } else {
                    pro.setImages(product.getImages());
                }

                ProductService proService = APIClient.getClient().create(ProductService.class);
                Call<Void> call = proService.update(pro, pro.getProductId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(ProductDetailActivity.this, ProductActivity.class);
                            Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Xác nhận");
                builder.setMessage("Bạn chắc chưa?");
                builder.setPositiveButton("Có", (dialog, which) -> {
                    ProductService proService = ProductRepository.getProductService();
                    Call<Void> call = proService.delete(product.getProductId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(ProductDetailActivity.this, ProductActivity.class);
                                Toast.makeText(getApplicationContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Lỗi", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                builder.setNegativeButton("Không", (dialog, which) -> dialog.cancel());
                builder.create().show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Mở trình chọn hình ảnh để người dùng chọn một hình từ thư viện.
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Vui lòng chọn hình ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                base64Image = encodeImageToBase64(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Lấy danh sách danh mục từ server và cập nhật giao diện Spinner,
    private void getCategories(int categoryId) {
        CategoryService cateService = CategoryRepository.getCategoryService();
        Call<List<Category>> call = cateService.getAllCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList = response.body();
                    if (categoryList != null && !categoryList.isEmpty()) {
                        List<String> categoryNames = new ArrayList<>();
                        for (Category category : categoryList) {
                            categoryNames.add(category.getName());
                        }
                        adapter.clear();
                        adapter.addAll(categoryNames);
                        adapter.notifyDataSetChanged();
                        spinner.setSelection(categoryId - 1);
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không thể tải dữ liệu danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("AddProductActivity", "Error fetching categories", t);
                Toast.makeText(ProductDetailActivity.this, "Error fetching categories", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
