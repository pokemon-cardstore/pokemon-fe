package com.example.pokemonshop.activity.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pokemonshop.R;
import com.example.pokemonshop.api.APIClient;
import com.example.pokemonshop.api.Category.CategoryRepository;
import com.example.pokemonshop.api.Category.CategoryService;
import com.example.pokemonshop.api.Product.ProductService;
import com.example.pokemonshop.model.Category;
import com.example.pokemonshop.model.Product;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnAdd;
    private EditText productName, productDes, productPrice, productQuantity;
    private ImageView imageView;
    private List<Category> categoryList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Spinner spinner;
    private String base64Image = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbarAddProductAdmin);
        toolbar.setTitle("Thêm sản phẩm");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        imageView = findViewById(R.id.product_image_admin_add);
        imageView.setImageResource(R.drawable.pikachu);
        imageView.setOnClickListener(v -> openImageChooser());

        btnAdd = findViewById(R.id.btnProductAdd);
        productName = findViewById(R.id.editProductName2);
        productDes = findViewById(R.id.editProductDescription2);
        productPrice = findViewById(R.id.editProductPrice2);
        productQuantity = findViewById(R.id.editProductQuantity2);
        spinner = findViewById(R.id.spinner);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        getCategories();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedCategoryId = categoryList.get(position).getCategoryId();
                setupAddButton(selectedCategoryId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupAddButton(int selectedCategoryId) {
        btnAdd.setOnClickListener(v -> {
            if (base64Image == null) {
                Toast.makeText(this, "Vui lòng chọn ảnh và đợi xử lý xong trước khi thêm sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            Product pro = new Product();
            pro.setName(productName.getText().toString().trim());
            pro.setDescription(productDes.getText().toString().trim());
            try {
                pro.setPrice(Double.parseDouble(productPrice.getText().toString().trim()));
                pro.setQuantity(Integer.parseInt(productQuantity.getText().toString().trim()));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập giá và số lượng hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            pro.setCategoryId(selectedCategoryId);

            if (base64Image != null) {
                List<Product.ProductImage> images = new ArrayList<>();
                images.add(new Product.ProductImage(null, base64Image));
                pro.setImages(images);
            }

            ProductService service = APIClient.getClient().create(ProductService.class);
            Call<Void> call = service.create(pro);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddProductActivity.this, ProductActivity.class));
                        finish();
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("AddProductActivity", "Error response: " + errorBody);
                            Toast.makeText(getApplicationContext(), "Thêm sản phẩm thất bại: " + errorBody, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Thêm sản phẩm thất bại và không thể đọc lỗi chi tiết", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AddProductActivity", "onFailure: ", t);
                }
            });
        });
    }

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
            new ProcessImageTask().execute(data.getData());
        }
    }

    private class ProcessImageTask extends AsyncTask<android.net.Uri, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(android.net.Uri... uris) {
            try (InputStream inputStream = getContentResolver().openInputStream(uris[0])) {
                return BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
                imageView.setImageBitmap(bitmap);
                new EncodeImageTask().execute(bitmap);
            }
        }
    }

    private class EncodeImageTask extends AsyncTask<Bitmap, Void, String> {
        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            return encodeImageToBase64(bitmaps[0]);
        }

        @Override
        protected void onPostExecute(String base64) {
            base64Image = base64;
            Log.d("AddProductActivity", "Base64 Image Length: " + base64Image.length());
            btnAdd.setEnabled(true);  // Kích hoạt lại nút Add sau khi xử lý xong
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);  // Giảm chất lượng ảnh xuống 50%
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getCategories() {
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
                        spinner.setSelection(0);
                    }
                } else {
                    Toast.makeText(AddProductActivity.this, "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("AddProductActivity", "Error fetching categories", t);
                Toast.makeText(AddProductActivity.this, "Error fetching categories", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
