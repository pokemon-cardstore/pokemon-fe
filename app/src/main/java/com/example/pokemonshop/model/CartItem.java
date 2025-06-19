package com.example.pokemonshop.model;

import java.io.Serializable;
import java.util.List;

public class CartItem implements Serializable {
    private int itemId;
    private int customerId;
    private int productId;
    private int quantity;

    private productVIew productVIew;

    public CartItem() {
    }

    public CartItem(int itemId, int customerId, int productId, int quantity, productVIew productVIew) {
        this.itemId = itemId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.productVIew = productVIew;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public productVIew getProductVIew() {
        return productVIew;
    }

    public void setProductVIew(productVIew productVIew) {
        this.productVIew = productVIew;
    }

    public static class productVIew implements Serializable {
        private int productId;
        private int categoryId;
        private String name;
        private String description;
        private double price;
        private int status;
        private int quantity;
        private com.example.pokemonshop.model.Product.Category category;
        private List<com.example.pokemonshop.model.Product.ProductImage> images;

        public productVIew() {
        }

        public productVIew(int productId, int categoryId, String name, String description, double price, int status, int quantity, com.example.pokemonshop.model.Product.Category category, List<com.example.pokemonshop.model.Product.ProductImage> images) {
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

        public com.example.pokemonshop.model.Product.Category getCategory() {
            return category;
        }

        public void setCategory(com.example.pokemonshop.model.Product.Category category) {
            this.category = category;
        }

        public List<com.example.pokemonshop.model.Product.ProductImage> getImages() {
            return images;
        }

        public void setImages(List<com.example.pokemonshop.model.Product.ProductImage> images) {
            this.images = images;
        }
    }
}
