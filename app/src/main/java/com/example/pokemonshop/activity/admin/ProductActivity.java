package com.example.pokemonshop.activity.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pokemonshop.R;
import com.example.pokemonshop.adapters.ProductAdapter;
import com.example.pokemonshop.api.Product.ProductRepository;
import com.example.pokemonshop.api.Product.ProductService;
import com.example.pokemonshop.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity{
    private ListView listViewProduct;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbarProductAdminHome);
        toolbar.setTitle("Các sản phẩm");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(v -> { // Xử lý sự kiện khi nhấn vào biểu tượng quay lại
            Intent intent = new Intent(ProductActivity.this, AdminDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            ProductActivity.this.finish();
        });

        listViewProduct= findViewById(R.id.listViewProduct);
        fab = findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        ProductService productService = ProductRepository.getProductService();
        Call<List<Product>> call = productService.getAllProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!response.isSuccessful()) {
                    Log.e("ProductActivity", "Error: " + response.code());
                    Toast.makeText(ProductActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Product> products = response.body();
                if (products == null || products.isEmpty()) {
                    Log.e("ProductActivity", "No data received");
                    Toast.makeText(ProductActivity.this, "Không có dữ liệu", Toast.LENGTH_LONG).show();
                    return;
                }

                // Set the adapter for the ListView
                listViewProduct.setAdapter(new ProductAdapter(products, getApplicationContext()));

                listViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Product product = (Product) parent.getItemAtPosition(position);
                        Call<Product> call = productService.find(product.getProductId());
                        call.enqueue(new Callback<Product>() {
                            @Override
                            public void onResponse(Call<Product> call, Response<Product> response) {
                                if(!response.isSuccessful()) {
                                    Log.e("ProductActivity", "Error: " + response.code());
                                    Toast.makeText(ProductActivity.this, "Error: " + response.code(), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Product productInfo = response.body();
                                if (productInfo == null) {
                                    Log.e("ProductActivity", "No data received");
                                    Toast.makeText(ProductActivity.this, "Không có dữ liệu", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Intent intent = new Intent(ProductActivity.this, ProductDetailActivity.class);
                                intent.putExtra("product", productInfo);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Call<Product> call, Throwable t) {
                                Log.e("ProductActivity", "Failed to get data: " + t.getMessage(), t);
                                Toast.makeText(ProductActivity.this, "Tải dữ liệu thất bại: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("ProductActivity", "Failed to load data: " + t.getMessage(), t);
                Toast.makeText(ProductActivity.this, "Tải dữ liệu thất bại: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        }
    }