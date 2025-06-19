package com.example.pokemonshop.model;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private int productId;
    private int categoryId;
    private String name;
    private String description;
    private double price;
    private int status;
    private int quantity;
    private Category category;
    private List<ProductImage> images;

    public Product() {
    }

    public Product(int productId, int categoryId, String name, String description, double price, int status, int quantity, Category category, List<ProductImage> images) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.quantity = quantity;
        this.category = category;
        this.images = images;
    }

    // Getters and setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    // Nested Category class
    public static class Category implements Serializable {
        private String name;
        private String description;

        public Category(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    // Nested ProductImage class
    public static class ProductImage implements Serializable {
        private String imagePath;
        private String base64StringImage;

        public ProductImage(String imagePath, String base64StringImage) {
            this.imagePath = imagePath;
            this.base64StringImage = base64StringImage;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getBase64StringImage() {
            return base64StringImage;
        }

        public void setBase64StringImage(String base64StringImage) {
            this.base64StringImage = base64StringImage;
        }
    }
}
